package cn.kettle.scheduler.kettle;

import java.util.Date;
import java.util.List;

public class KettleJobBean {
	/** 任务状态，启动 */
	public static final Integer STATE_START=1;
	/** 任务状态，停止 */
	public static final Integer STATE_STOP=0;
	/** 任务运行状态，正常结束 */
	public static final Integer RUNSTATUS_SUCCESS = 0;
	/** 任务运行状态，运行中 */
	public static final Integer RUNSTATUS_RUNNING = 1;
	/** 任务运行状态，异常结束 */
	public static final Integer RUNSTATUS_ERROR = 2;
    /** 任务ID，主键 */
    private String jobId;
    /** 目录，相对于资料库根目录的绝对路径 */
    private String directory;
    /** 任务名称 */
    private String name;
    /** 作业描述 */
    private String description;
    /** 作业扩展描述 */
    private String extendedDescription;
    /** 状态：0=停止，1=启动 */
    private Integer state;
    /** 表达式 */
    private String exp;
    /** 创建人 */
    private String createdUser;
    /** 创建日期时间 */
    private Date createdTime;
    /** 修改人 */
    private String modifiedUser;
    /** 修改日期时间 */
    private Date modifiedTime;
    /** 最后运行状态，0：正常结束，1：运行中，2：异常结束 */
    private Integer runStatus;
    /** 最后更新时间 */
    private Date lastUpdate;
    /** 自动重启次数 */
    private Integer autoRestartNum;
    /** 仅用于前后端交互时接收参数配置 */
    List<KettleJobParamBean> paramConfigs;
    /** 当前调度状态，仅用于显示在调度器中的实时状态 */
    private String scheduledState;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExtendedDescription() {
        return extendedDescription;
    }

    public void setExtendedDescription(String extendedDescription) {
        this.extendedDescription = extendedDescription;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(String createdUser) {
        this.createdUser = createdUser;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getModifiedUser() {
        return modifiedUser;
    }

    public void setModifiedUser(String modifiedUser) {
        this.modifiedUser = modifiedUser;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public Integer getRunStatus() {
        return runStatus;
    }

    public void setRunStatus(Integer runStatus) {
        this.runStatus = runStatus;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Integer getAutoRestartNum() {
        return autoRestartNum;
    }

    public void setAutoRestartNum(Integer autoRestartNum) {
        this.autoRestartNum = autoRestartNum;
    }

	/**
	 * @return the paramConfigs
	 */
	public List<KettleJobParamBean> getParamConfigs() {
		return paramConfigs;
	}

	/**
	 * @param paramConfigs the paramConfigs to set
	 */
	public void setParamConfigs(List<KettleJobParamBean> paramConfigs) {
		this.paramConfigs = paramConfigs;
	}
	
	/**
	 * 获取Quartz中任务的名称，默认为name.jobId
	 * @return
	 */
	public String getQuartzJobName(){
		return name+"."+jobId;
	}
	
	/**
	 * @return the scheduledState
	 */
	public String getScheduledState() {
		return scheduledState;
	}

	/**
	 * @param scheduledState the scheduledState to set
	 */
	public void setScheduledState(String scheduledState) {
		this.scheduledState = scheduledState;
	}
	
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("KettleJobBean{");
        sb.append("jobId='").append(jobId).append('\'');
        sb.append(", directory='").append(directory).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", extendedDescription='").append(extendedDescription).append('\'');
        sb.append(", state=").append(state);
        sb.append(", exp='").append(exp).append('\'');
        sb.append(", createdUser='").append(createdUser).append('\'');
        sb.append(", createdTime=").append(createdTime);
        sb.append(", modifiedUser='").append(modifiedUser).append('\'');
        sb.append(", modifiedTime=").append(modifiedTime);
        sb.append(", runStatus=").append(runStatus);
        sb.append(", lastUpdate=").append(lastUpdate);
        sb.append(", autoRestartNum=").append(autoRestartNum);
        sb.append(", scheduledState=").append(scheduledState);
        sb.append('}');
        return sb.toString();
    }

	


}
