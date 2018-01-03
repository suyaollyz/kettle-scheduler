package cn.kettle.scheduler.kettle;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.FileLoggingEventListener;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.trans.Trans;

import cn.kettle.scheduler.dao.KettleJobDao;
import cn.kettle.scheduler.dao.KettleJobRunLogDao;
import cn.kettle.scheduler.commons.util.Logger;

public class KettleJobRunner {
	private static Logger logger = new Logger();
	private FileLoggingEventListener fileAppender;
	private Job job;
	private KettleJobDao kettleJobDao;
	private KettleJobRunLogDao kettleJobRunLogDao;
	private String runLogRootPath;
	private String runLogFileName;
	/**
	 * 启动一个Kettle Job
	 * @param kettleJobInfo 任务配置信息
	 * @param parameters 任务配置的默认参数
	 */
    public void start(KettleJobBean kettleJobInfo, Map<String, String> parameters){
    	// 任务运行状态更新为运行中
        kettleJobInfo.setRunStatus(KettleJobBean.RUNSTATUS_RUNNING);
        int updateCount = kettleJobDao.updateJobToRunning(kettleJobInfo);
        // 如果更新失败，则直接返回，不运行
        if(updateCount!=1){
        	logger.error("修改数据库中任务状态失败，被修改记录数：", updateCount);
        	return;
        }
    	// Kettle任务开始运行
    	Date startTime = new Date();
    	KettleJobRunLogBean runLog = new KettleJobRunLogBean();
        runLog.setLogId(UUID.randomUUID().toString().replaceAll("-", ""));
        runLog.setJobId(kettleJobInfo.getJobId());
        runLog.setStartTime(startTime);
        
    	String jobName = kettleJobInfo.getName();
    	String directory = kettleJobInfo.getDirectory();
        try {
        	// 加载默认配置参数
            JobMeta jobMeta = KettleUtil.loadJob(jobName, directory);
            StringBuilder sb = new StringBuilder("参数列表：\n");
            for(String param : jobMeta.listParameters()) {
                if(parameters.containsKey(param)){
                    logger.debug("Kettle任务"+directory+"/"+jobName+"设置参数："+param+"="+parameters.get(param));
                    jobMeta.setParameterValue(param, parameters.get(param));
                }
                sb.append(param).append("，default=").append(jobMeta.getParameterDefault(param));
                sb.append("，description=").append(jobMeta.getParameterDescription(param));
                sb.append("，current value=").append(jobMeta.getParameterValue(param));
                sb.append("\n");
            }
            logger.debug(sb);
            job = new Job( jobMeta.getRepository(), jobMeta );
            job.copyParametersFrom( job.getJobMeta() );
            // Put the parameters over the already defined variable space. Parameters get priority.
            job.activateParameters();
            logger.debug("开始执行KettleJob：", job);
            // 启动任务日志记录对象
            startKettleJobRunLogWriter();
            // 插入执行记录表
            runLog.setLogFile(runLogFileName);
            runLog.setResult(Trans.STRING_RUNNING);
            kettleJobRunLogDao.insertLog(runLog);
            // 启动任务
            job.start();
            job.waitUntilFinished();
            
            Result result = job.getResult();
            if(result != null && result.getNrErrors() != 0){
            	kettleJobInfo.setRunStatus(KettleJobBean.RUNSTATUS_ERROR);
            }else{
            	kettleJobInfo.setRunStatus(KettleJobBean.RUNSTATUS_SUCCESS);
            }
            stopKettleJobRunLogWriter();
        } catch (Exception e) {
            logger.error("Kettle任务"+directory+"/"+jobName+"未执行，异常", e);
            kettleJobInfo.setRunStatus(KettleJobBean.RUNSTATUS_ERROR);
        }
        Date endTime = new Date();
        // 更新执行结果
        runLog.setEndTime(endTime);
        runLog.setResult(job.getStatus());
        kettleJobRunLogDao.updateLog(runLog);
        // 更新任务运行状态
        kettleJobDao.updateJobToStopped(kettleJobInfo);
        job = null;
        fileAppender = null;
    }
    /**
     * 启动日志输出
     */
    public void startKettleJobRunLogWriter(){
    	Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		sdf.applyPattern("yyyy-MM-dd");
		String today = sdf.format(date);
		sdf.applyPattern("HHmmssSSS");
		String time = sdf.format(date);
		// 创建日志文件
		File rootPath = new File(runLogRootPath+File.separator+today);
		if(!rootPath.exists()){
			rootPath.mkdirs();
		}
		runLogFileName = runLogRootPath+File.separator+today+File.separator+job.toString()+"_"+time+".log";
		try {
			fileAppender = new FileLoggingEventListener(job.getLogChannelId(), runLogFileName, true );
			KettleLogStore.init(0, 0);
			KettleLogStore.getAppender().addLoggingEventListener(fileAppender);
		} catch (KettleException e) {
			logger.error("创建Kettle日志文件失败", e);
		}
    }
    /**
     * 停止日志输出
     */
    public void stopKettleJobRunLogWriter(){
    	if(fileAppender!=null){
    		try {
				fileAppender.close();
			} catch (KettleException e) {
				logger.error("关闭Kettle日志文件失败", e);
			}
    		KettleLogStore.getAppender().removeLoggingEventListener(fileAppender);
    	}
    }
    
    public void stopKettleJob(){
    	job.stopAll();
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
	/**
	 * @return the runLogRootPath
	 */
	public String getRunLogRootPath() {
		return runLogRootPath;
	}
	/**
	 * @param runLogRootPath the runLogRootPath to set
	 */
	public void setRunLogRootPath(String runLogRootPath) {
		this.runLogRootPath = runLogRootPath;
	}
    
}
