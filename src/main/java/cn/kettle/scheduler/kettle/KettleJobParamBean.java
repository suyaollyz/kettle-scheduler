package cn.kettle.scheduler.kettle;

import java.util.Date;

public class KettleJobParamBean {
	/** 参数状态，启动 */
	public static final Integer STATE_START=1;
	/** 参数状态，停止 */
	public static final Integer STATE_STOP=0;
	/** 参数ID，主键 */
	private String paramId;
	/** 任务ID，对应KETTLE_JOB表的JOB_ID */
	private String jobId;
	/** 参数名 */
	private String paramCode;
	/** 参数值 */
	private String paramValue;
	/** 参数描述 */
	private String description;
	/** 参数描述扩展 */
	private String extendedDescription;
	/** 参数状态：0=禁用：1=启用，2=删除 */
	private Integer state;
	/** 创建人 */
	private String createdUser;
	/** 创建日期时间 */
	private Date createdTime;
	/** 修改人 */
	private String modifiedUser;
	/** 修改日期时间 */
	private Date modifiedTime;
	/**
	 * @return the paramId
	 */
	public String getParamId() {
		return paramId;
	}
	/**
	 * @param paramId the paramId to set
	 */
	public void setParamId(String paramId) {
		this.paramId = paramId;
	}
	/**
	 * @return the jobId
	 */
	public String getJobId() {
		return jobId;
	}
	/**
	 * @param jobId the jobId to set
	 */
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	/**
	 * @return the paramCode
	 */
	public String getParamCode() {
		return paramCode;
	}
	/**
	 * @param paramCode the paramCode to set
	 */
	public void setParamCode(String paramCode) {
		this.paramCode = paramCode;
	}
	/**
	 * @return the paramValue
	 */
	public String getParamValue() {
		return paramValue;
	}
	/**
	 * @param paramValue the paramValue to set
	 */
	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the extendedDescription
	 */
	public String getExtendedDescription() {
		return extendedDescription;
	}
	/**
	 * @param extendedDescription the extendedDescription to set
	 */
	public void setExtendedDescription(String extendedDescription) {
		this.extendedDescription = extendedDescription;
	}
	/**
	 * @return the state
	 */
	public Integer getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(Integer state) {
		this.state = state;
	}
	/**
	 * @return the createdUser
	 */
	public String getCreatedUser() {
		return createdUser;
	}
	/**
	 * @param createdUser the createdUser to set
	 */
	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}
	/**
	 * @return the createdTime
	 */
	public Date getCreatedTime() {
		return createdTime;
	}
	/**
	 * @param createdTime the createdTime to set
	 */
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	/**
	 * @return the modifiedUser
	 */
	public String getModifiedUser() {
		return modifiedUser;
	}
	/**
	 * @param modifiedUser the modifiedUser to set
	 */
	public void setModifiedUser(String modifiedUser) {
		this.modifiedUser = modifiedUser;
	}
	/**
	 * @return the modifiedTime
	 */
	public Date getModifiedTime() {
		return modifiedTime;
	}
	/**
	 * @param modifiedTime the modifiedTime to set
	 */
	public void setModifiedTime(Date modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("KettleJobParamBean{");
		sb.append("paramId='").append(paramId).append('\'');
		sb.append(", jobId='").append(jobId).append('\'');
		sb.append(", paramCode='").append(paramCode).append('\'');
		sb.append(", paramValue='").append(paramValue).append('\'');
		sb.append(", description='").append(description).append('\'');
		sb.append(", extendedDescription='").append(extendedDescription).append('\'');
		sb.append(", state=").append(state);
		sb.append(", createdUser='").append(createdUser).append('\'');
		sb.append(", createdTime=").append(createdTime);
		sb.append(", modifiedUser='").append(modifiedUser).append('\'');
		sb.append(", modifiedTime=").append(modifiedTime);
		sb.append('}');
		return sb.toString();
	}
}
