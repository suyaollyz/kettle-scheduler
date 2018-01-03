package cn.kettle.scheduler.dao;

import java.util.List;

import cn.kettle.scheduler.kettle.KettleJobBean;

public interface KettleJobDao {
	/**
	 * 查找数据库中所有任务记录
	 * @return
	 */
	public List<KettleJobBean> selectAllJob();
	/**
	 * 在数据库中查找对应的任务详细信息
	 * @param jobId
	 * @return
	 */
	public KettleJobBean selectJobById(String jobId);
	/**
	 * 向数据库中插入一条Kettle Job记录
	 * @param kettleJob
	 */
	public int insertJob(KettleJobBean kettleJob);
	/**
	 * 修改数据库中一条kettle job记录
	 * @param kettleJob
	 */
	public int updateJob(KettleJobBean kettleJob);
	/**
	 * 从数据库删除一条kettle job记录，逻辑删除，把状态修改为2
	 * @param jobId
	 * @return
	 */
	public int deleteJob(String jobId);
	/**
	 * 修改Kettle job的运行状态为运行中
	 * @param kettleJobInfo
	 */
	public int updateJobToRunning(KettleJobBean kettleJobInfo);
	/**
	 * 修改Kettle job的运行状态为结束（成功结束或者异常结束）
	 * @param kettleJobInfo
	 */
	public int updateJobToStopped(KettleJobBean kettleJobInfo);
}
