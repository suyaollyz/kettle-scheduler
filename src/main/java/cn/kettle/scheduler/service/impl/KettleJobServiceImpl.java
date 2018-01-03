package cn.kettle.scheduler.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryElementMetaInterface;
import org.quartz.SchedulerException;
import org.quartz.UnableToInterruptJobException;

import cn.kettle.scheduler.commons.util.Logger;
import cn.kettle.scheduler.dao.KettleJobDao;
import cn.kettle.scheduler.dao.KettleJobParamsDao;
import cn.kettle.scheduler.dao.KettleJobRunLogDao;
import cn.kettle.scheduler.kettle.KettleJobBean;
import cn.kettle.scheduler.kettle.KettleJobParamBean;
import cn.kettle.scheduler.kettle.KettleJobRunLogBean;
import cn.kettle.scheduler.kettle.KettleUtil;
import cn.kettle.scheduler.scheduler.KettleJobLoader;
import cn.kettle.scheduler.service.KettleJobService;

public class KettleJobServiceImpl implements KettleJobService {
	private static Logger logger = new Logger();
	KettleJobLoader kettleJobLoader;
	KettleJobDao kettleJobDao;
	KettleJobParamsDao kettleJobParamsDao;
	KettleJobRunLogDao kettleJobRunLogDao;
	
	public List<KettleJobBean> selectAllJob() {
		List<KettleJobBean> jobList = kettleJobDao.selectAllJob();
		for(KettleJobBean kettleJob : jobList){
			String scheduledState = kettleJobLoader.getScheduledState(kettleJob.getQuartzJobName());
			kettleJob.setScheduledState(scheduledState);
		}
		return jobList;
	}
	@Override
	public List<Map<String, Object>> listRepoObject(String directoryName) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			Repository repository = KettleUtil.getRepository();
			RepositoryDirectoryInterface direction = repository.findDirectory(directoryName);
			List<RepositoryDirectoryInterface> children = direction.getChildren();
			for(RepositoryDirectoryInterface child : children){
				Map<String, Object> folder = new HashMap<String, Object>();
				Map<String, Object> folderAttrs = new HashMap<String, Object>();
				folderAttrs.put("nodeType", "0"); // 0表示folder，1表示job，2表示转换
				folder.put("id", child.getObjectId().getId());
				folder.put("text", child.getName());
				folder.put("state", "closed");
				folder.put("attributes", folderAttrs);
				result.add(folder);
			}
			// list jobs
			List<RepositoryElementMetaInterface> jobObjects = repository.getJobObjects(direction.getObjectId(),false);
			for(RepositoryElementMetaInterface jobObject : jobObjects){
				Map<String, Object> job = new HashMap<String, Object>();
				Map<String, Object> jobAttrs = new HashMap<String, Object>();
				jobAttrs.put("nodeType", "1"); // 0表示folder，1表示job，2表示转换
				job.put("id", jobObject.getObjectId().getId());
				job.put("text", jobObject.getName());
				job.put("iconCls", "icon-kettle-job-tree");
				job.put("attributes", jobAttrs);
				// 获取job配置的参数
				JobMeta jobMeta = repository.loadJob(jobObject.getObjectId(), null);
				List<KettleJobParamBean> params = new ArrayList<KettleJobParamBean>();
				for(String param : jobMeta.listParameters()) {
					KettleJobParamBean jobParam = new KettleJobParamBean();
					jobParam.setParamCode(param);
					jobParam.setParamValue(jobMeta.getParameterDefault(param));
					jobParam.setDescription(jobMeta.getParameterDescription(param));
					jobParam.setState(KettleJobParamBean.STATE_START);
					params.add(jobParam);
	            }
				jobAttrs.put("params", params);
				result.add(job);
			}
		} catch (KettleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	@Override
	public KettleJobBean findJob(String jobId) {
		return kettleJobDao.selectJobById(jobId);
	}
	@Override
	public int addJob(KettleJobBean kettleJob) {
		if(kettleJob==null){
			throw new NullPointerException("Kettle任务对象为空");
		}
		// 保存任务信息
		kettleJob.setJobId(UUID.randomUUID().toString().replaceAll("-", ""));
		int insertResult = kettleJobDao.insertJob(kettleJob);
		if(insertResult!=1){
			throw new RuntimeException("Kettle任务保存失败");
		}
		// 保存任务默认参数
		List<KettleJobParamBean> paramConfigs = kettleJob.getParamConfigs();
		if(paramConfigs!=null && !paramConfigs.isEmpty()){
			for(KettleJobParamBean param : paramConfigs){
				param.setParamId(UUID.randomUUID().toString().replaceAll("-", ""));
				param.setJobId(kettleJob.getJobId());
				kettleJobParamsDao.insertParam(param);
			}
		}
		// 如果任务状态为启动，则加入到调度器中
		if(KettleJobBean.STATE_START.equals(kettleJob.getState())){
			try {
				kettleJobLoader.addJob(kettleJob);
			} catch (SchedulerException e) {
				logger.error("在调度中新增Kettle任务失败", e);
				throw new RuntimeException("在调度中新增Kettle任务失败", e);
			}
		}else{
			logger.error("Kettle任务状态异常，只允许：0-停止，1-启动");
			throw new IllegalStateException("Kettle任务状态异常，只允许：0-停止，1-启动");
		}
		return insertResult;
	}
	@Override
	public int editJob(KettleJobBean kettleJob) {
		if(KettleJobBean.STATE_STOP.equals(kettleJob.getState())){
			try {
				kettleJobLoader.removeJob(kettleJob);
			} catch (SchedulerException e) {
				logger.error("从调度中移除Kettle任务失败", e);
				throw new RuntimeException("从调度中移除Kettle任务失败", e);
			}
		}else if(KettleJobBean.STATE_START.equals(kettleJob.getState())){
			try {
				kettleJobLoader.addJob(kettleJob);
			} catch (SchedulerException e) {
				logger.error("在调度中新增Kettle任务失败", e);
				throw new RuntimeException("在调度中新增Kettle任务失败", e);
			}
		}else{
			logger.error("任务状态异常，只允许：0-停止，1-启动");
			throw new IllegalStateException("任务状态异常，只允许：0-停止，1-启动");
		}
		return kettleJobDao.updateJob(kettleJob);
	}
	
	@Override
	public int removeJob(String jobId) {
		// 根据jobId查找对应的任务
		KettleJobBean job = kettleJobDao.selectJobById(jobId);
		if(job==null){
			NullPointerException e = new NullPointerException("根据任务ID未找到对应的数据库记录："+jobId);
			logger.error(e);
			throw e;
		}
		try {
			kettleJobLoader.removeJob(job);
		} catch (SchedulerException e) {
			logger.error("从调度中移除Kettle任务失败", e);
			throw new RuntimeException("从调度中移除Kettle任务失败", e);
		}
		int deleteResult = kettleJobDao.deleteJob(jobId);
		if(deleteResult==0){
			throw new RuntimeException("Kettle任务删除失败");
		}
		return deleteResult;
	}
	
	/**
	 * 根据任务ID，查找对应的默认参数配置
	 * @param jobId
	 * @return
	 */
	@Override
	public List<KettleJobParamBean> findParams(String jobId) {
		// 根据jobId查找对应的任务
		KettleJobBean job = kettleJobDao.selectJobById(jobId);
		Set<String> paramSet = new HashSet<String>();
		// 获取job配置的参数
		List<KettleJobParamBean> params = kettleJobParamsDao.selectParamsByJobId(jobId);
		if(params!=null && !params.isEmpty()){
			for(KettleJobParamBean param : params){
				paramSet.add(param.getParamCode());
			}
		}
		// 读取任务中配置的参数
		try{
			JobMeta jobMeta = KettleUtil.loadJob(job.getName(), job.getDirectory());
			for(String param : jobMeta.listParameters()) {
				if(paramSet.contains(param)){
					continue;
				}
				KettleJobParamBean jobParam = new KettleJobParamBean();
				jobParam.setParamCode(param);
				jobParam.setParamValue(jobMeta.getParameterDefault(param));
				jobParam.setDescription(jobMeta.getParameterDescription(param));
				jobParam.setState(KettleJobParamBean.STATE_START);
				params.add(jobParam);
	        }
		} catch (KettleException e){
			logger.error("读取任务文件中配置的参数失败", e);
		}
		return params;
	}
	
	@Override
	public int editParams(KettleJobBean kettleJob) {
		int deleteResult = 0;
		// 遍历保存，如果存在，则修改，如果不存在，则新增
		List<KettleJobParamBean> paramConfigs = kettleJob.getParamConfigs();
		if(paramConfigs!=null && !paramConfigs.isEmpty()){
			for(KettleJobParamBean param : paramConfigs){
				if(param.getParamId()!=null && !param.getParamId().isEmpty()){
					KettleJobParamBean oldParam = kettleJobParamsDao.selectParamById(param.getParamId());
					if(oldParam!=null){
						deleteResult+=kettleJobParamsDao.updateParam(param);
						continue;
					}
				}else{
					param.setParamId(UUID.randomUUID().toString().replaceAll("-", ""));
					param.setJobId(kettleJob.getJobId());
				}
				deleteResult+=kettleJobParamsDao.insertParam(param);
			}
		}
		return deleteResult;
	}
	
	/**
	 * 删除指定的任务参数
	 * @param paramId
	 * @return
	 */
	@Override
	public int removeParam(String paramId){
		return kettleJobParamsDao.deleteParam(paramId);
	}
	
	/**
	 * 重新调度所有Kettle任务
	 */
	@Override
	public void reloadJobs() {
		kettleJobLoader.reloadJobs();
	}
	/**
	 * 重新调度指定任务
	 * @param jobId
	 */
	@Override
	public void reloadJob(String jobId) {
		KettleJobBean job = kettleJobDao.selectJobById(jobId);
		if(job==null){
			NullPointerException e = new NullPointerException("根据任务ID未找到对应的数据库记录："+jobId);
			logger.error(e);
			throw e;
		}
		try {
			kettleJobLoader.addJob(job);
		} catch (SchedulerException e) {
			logger.error("在调度中重新调度Kettle任务失败", job, e);
			throw new RuntimeException("在调度中重新调度Kettle任务失败", e);
		}
	}
	/**
	 * 根据任务ID，查找该任务所有运行记录
	 * @param jobId 任务ID
	 * @param fromTime 查询起始时间
	 * @param toTime 查询结束时间
	 * @param page 查询第几页
	 * @param rows 查询每一页的行数
	 * @return
	 */
	public List<KettleJobRunLogBean> findRunLogs(String jobId, String fromTime, String toTime, Integer page, Integer rows){
		return kettleJobRunLogDao.selectLogsByJobId(jobId, fromTime, toTime, page, rows);
	}
	/**
	 * 根据任务ID，查找该任务所有运行记录数
	 * @param jobId 任务ID
	 * @param fromTime 查询起始时间
	 * @param toTime 查询结束时间
	 * @return
	 */
	public int findRunLogsCount(String jobId, String fromTime, String toTime){
		return kettleJobRunLogDao.countLogsByJobId(jobId, fromTime, toTime);
	}
	
	/**
	 * 根据运行日志ID，获取运行日志记录
	 * @param logId
	 * @return
	 */
	public KettleJobRunLogBean findRunLog(String logId){
		return kettleJobRunLogDao.selectLogById(logId);
	}
	/**
	 * 暂停任务，被暂停任务将不再执行下一次，当前如果正在执行，将本次会执行完成。
	 * @param jobId 要被暂停的任务ID
	 */
	@Override
	public void pauseJob(String jobId) {
		KettleJobBean job = kettleJobDao.selectJobById(jobId);
		if(job==null){
			NullPointerException e = new NullPointerException("根据任务ID未找到对应的数据库记录："+jobId);
			logger.error(e);
			throw e;
		}
		try {
			kettleJobLoader.pauseJob(job);
		} catch (SchedulerException e) {
			logger.error("暂停任务失败", job, e);
			throw new RuntimeException("暂停任务失败", e);
		}
	}
	/**
	 * 中断任务，任务当前如果正在执行，则强制停止执行
	 * @param jobId 要被中断的任务ID
	 * @return 中断成功返回true，失败返回false
	 */
	public boolean interruptJob(String jobId){
		KettleJobBean job = kettleJobDao.selectJobById(jobId);
		if(job==null){
			NullPointerException e = new NullPointerException("根据任务ID未找到对应的数据库记录："+jobId);
			logger.error(e);
			throw e;
		}
		try {
			return kettleJobLoader.interruptJob(job);
		} catch (UnableToInterruptJobException e) {
			logger.error("中断任务失败", job, e);
			return false;
		}
	}
	
	/**
	 * 恢复任务调度
	 * @param jobId 要被恢复调度的任务ID
	 */
	public void resumeJob(String jobId){
		KettleJobBean job = kettleJobDao.selectJobById(jobId);
		if(job==null){
			NullPointerException e = new NullPointerException("根据任务ID未找到对应的数据库记录："+jobId);
			logger.error(e);
			throw e;
		}
		try {
			kettleJobLoader.resumeJob(job);
		} catch (SchedulerException e) {
			logger.error("恢复任务调度失败", job, e);
			throw new RuntimeException("恢复任务调度失败", e);
		}
	}
	
	/**
	 * 修改任务运行状态，仅限于由于系统异常导致的运行状态未更改的情况，其他情况可能会导致任务运行混乱，请谨慎使用
	 * @param jobId 任务ID
	 * @param runStatus 修改后的任务运行状态
	 * @return 返回被修改的记录数
	 */
	public int editJobRunStatus(String jobId, Integer runStatus){
		if(runStatus==null){
			IllegalArgumentException e = new IllegalArgumentException("传入的修改后运行状态为空");
			logger.error(e);
			throw e;
		}
		KettleJobBean job = kettleJobDao.selectJobById(jobId);
		if(job==null){
			NullPointerException e = new NullPointerException("根据任务ID未找到对应的数据库记录："+jobId);
			logger.error(e);
			throw e;
		}
		job.setRunStatus(runStatus);
		return kettleJobDao.updateJobToStopped(job);
	}
	/**
	 * @return the kettleJobLoader
	 */
	public KettleJobLoader getKettleJobLoader() {
		return kettleJobLoader;
	}
	/**
	 * @param kettleJobLoader the kettleJobLoader to set
	 */
	public void setKettleJobLoader(KettleJobLoader kettleJobLoader) {
		this.kettleJobLoader = kettleJobLoader;
	}
	/**
	 * @return the kettleJobDao
	 */
	public KettleJobDao getKettleJobDao() {
		return kettleJobDao;
	}
	/**
	 * @param kettleJobDao the kettleJobDao to set
	 */
	public void setKettleJobDao(KettleJobDao kettleJobDao) {
		this.kettleJobDao = kettleJobDao;
	}
	/**
	 * @return the kettleJobParamsDao
	 */
	public KettleJobParamsDao getKettleJobParamsDao() {
		return kettleJobParamsDao;
	}
	/**
	 * @param kettleJobParamsDao the kettleJobParamsDao to set
	 */
	public void setKettleJobParamsDao(KettleJobParamsDao kettleJobParamsDao) {
		this.kettleJobParamsDao = kettleJobParamsDao;
	}
	/**
	 * @return the kettleJobRunLogDao
	 */
	public KettleJobRunLogDao getKettleJobRunLogDao() {
		return kettleJobRunLogDao;
	}
	/**
	 * @param kettleJobRunLogDao the kettleJobRunLogDao to set
	 */
	public void setKettleJobRunLogDao(KettleJobRunLogDao kettleJobRunLogDao) {
		this.kettleJobRunLogDao = kettleJobRunLogDao;
	}
}
