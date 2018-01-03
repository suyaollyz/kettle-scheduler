package cn.kettle.scheduler.service;

import java.util.List;
import java.util.Map;

import cn.kettle.scheduler.kettle.KettleJobBean;
import cn.kettle.scheduler.kettle.KettleJobParamBean;
import cn.kettle.scheduler.kettle.KettleJobRunLogBean;

public interface KettleJobService {
	/**
	 * 查询所有配置的KettleJob信息
	 * @return
	 */
	public List<KettleJobBean> selectAllJob();

	public List<Map<String, Object>> listRepoObject(String directoryName);

	/**
	 * 根据jobId查找对应的任务详细信息
	 * @param jobId
	 * @return 如果没有，则返回null
	 */
	public KettleJobBean findJob(String jobId);
	
	/**
	 * 新增一条Kettle Job
	 * @param kettleJob
	 */
	public int addJob(KettleJobBean kettleJob);

	/**
	 * 编辑保存一条kettle job
	 * @param kettleJob
	 */
	public int editJob(KettleJobBean kettleJob);

	/**
	 * 根据jobId删除对应的任务
	 * @param jobId
	 * @return
	 */
	public int removeJob(String jobId);
	
	/**
	 * 根据任务ID，查找对应的默认参数配置
	 * @param jobId
	 * @return
	 */
	public List<KettleJobParamBean> findParams(String jobId);
	
	/**
	 * 编辑保存任务的默认参数配置
	 */
	public int editParams(KettleJobBean kettleJob);

	/**
	 * 删除指定的任务参数
	 * @param paramId
	 * @return
	 */
	public int removeParam(String paramId);
	
	/**
	 * 重新调度所有任务
	 */
	public void reloadJobs();
	
	/**
	 * 重新调度指定任务
	 * @param jobId
	 */
	public void reloadJob(String jobId);

	/**
	 * 根据任务ID，查找该任务所有运行记录
	 * @param jobId 任务ID
	 * @param fromTime 查询起始时间
	 * @param toTime 查询结束时间
	 * @param page 查询第几页
	 * @param rows 查询每一页的行数
	 * @return
	 */
	public List<KettleJobRunLogBean> findRunLogs(String jobId, String fromTime, String toTime, Integer page, Integer rows);
	/**
	 * 根据任务ID，查找该任务所有运行记录数
	 * @param jobId 任务ID
	 * @param fromTime 查询起始时间
	 * @param toTime 查询结束时间
	 * @return
	 */
	public int findRunLogsCount(String jobId, String fromTime, String toTime);
	/**
	 * 根据运行日志ID，获取运行日志记录
	 * @param logId
	 * @return
	 */
	public KettleJobRunLogBean findRunLog(String logId);

	/**
	 * 暂停任务，被暂停任务将不再执行下一次，当前如果正在执行，将本次会执行完成。
	 * @param jobId 要被暂停的任务ID
	 */
	public void pauseJob(String jobId);
	
	/**
	 * 中断任务，任务当前如果正在执行，则强制停止执行
	 * @param jobId 要被中断的任务ID
	 * @return 中断成功返回true，失败返回false
	 */
	public boolean interruptJob(String jobId);
	
	/**
	 * 恢复任务调度
	 * @param jobId 要被恢复调度的任务ID
	 */
	public void resumeJob(String jobId);

	/**
	 * 修改任务运行状态，仅限于由于系统异常导致的运行状态未更改的情况，其他情况可能会导致任务运行混乱，请谨慎使用
	 * @param jobId 任务ID
	 * @param runStatus 修改后的任务运行状态
	 * @return 返回被修改的记录数
	 */
	public int editJobRunStatus(String jobId, Integer runStatus);
}
