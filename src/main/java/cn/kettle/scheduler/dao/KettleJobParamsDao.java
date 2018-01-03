package cn.kettle.scheduler.dao;

import java.util.List;

import cn.kettle.scheduler.kettle.KettleJobParamBean;

public interface KettleJobParamsDao {
	/**
	 * 根据参数ID，查询指定的参数信息
	 * @param paramId 
	 * @return
	 */
	KettleJobParamBean selectParamById(String paramId);
	/**
	 * 根据任务ID，查找对应的默认参数配置
	 * @param jobId
	 * @return
	 */
	List<KettleJobParamBean> selectParamsByJobId(String jobId);

	/**
	 * 向数据库中插入一条参数配置记录
	 * @param param
	 * @return
	 */
	int insertParam(KettleJobParamBean param);
	
	/**
	 * 更新指定的参数信息
	 * @param param
	 */
	int updateParam(KettleJobParamBean param);

	/**
	 * 在数据库中删除指定的参数
	 * @param paramId
	 * @return 删除记录数
	 */
	int deleteParam(String paramId);
	
	/**
	 * 删除任务ID下的所有参数配置
	 * @param jobId
	 * @return
	 */
	int deleteParamsByJobId(String jobId);


}
