/**
 * 
 */
package cn.kettle.scheduler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.kettle.scheduler.kettle.KettleJobRunLogBean;

/**
 * Kettle Job运行日志记录Dao
 *
 */
public interface KettleJobRunLogDao {
	/**
	 * 根据运行ID，查找指定的运行记录
	 * @param logId
	 * @return
	 */
	public KettleJobRunLogBean selectLogById(String logId);
	/**
	 * 根据任务ID，查找任务的所有运行记录
	 * @param jobId 任务ID
	 * @param fromTime 查询起始时间
	 * @param toTime 查询结束时间
	 * @param page 查询第几页
	 * @param rows 查询每一页的行数
	 * @return
	 */
	public List<KettleJobRunLogBean> selectLogsByJobId(
			@Param("jobId")String jobId, 
			@Param("fromTime")String fromTime, 
			@Param("toTime")String toTime, 
			@Param("page")Integer page, 
			@Param("rows")Integer rows);
	/**
	 * 根据任务ID，查找该任务所有运行记录数
	 * @param jobId 任务ID
	 * @param fromTime 查询起始时间
	 * @param toTime 查询结束时间
	 * @return
	 */
	public int countLogsByJobId(
			@Param("jobId")String jobId, 
			@Param("fromTime")String fromTime, 
			@Param("toTime")String toTime);
	/**
	 * 在数据库新增一条运行日志
	 * @param runLog
	 * @return
	 */
	public int insertLog(KettleJobRunLogBean runLog);
	/**
	 * 更新运行日志记录信息
	 * @param runLog
	 */
	public int updateLog(KettleJobRunLogBean runLog);
}
