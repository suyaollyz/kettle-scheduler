package cn.kettle.scheduler.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.UnableToInterruptJobException;
import org.quartz.impl.matchers.GroupMatcher;

import cn.kettle.scheduler.commons.util.Logger;
import cn.kettle.scheduler.dao.KettleJobDao;
import cn.kettle.scheduler.dao.KettleJobParamsDao;
import cn.kettle.scheduler.kettle.KettleJobBean;
import cn.kettle.scheduler.kettle.KettleJobParamBean;
import cn.kettle.scheduler.kettle.KettleQuartzJob;

public class KettleJobLoader implements JobLoader {
	private static Logger logger = new Logger();
	private Scheduler scheduler;
	KettleJobRunnerFactory kettleJobRunnerFactory;
	KettleJobDao kettleJobDao;
	KettleJobParamsDao kettleJobParamsDao;
	
	/**
	 * 加载Kettle任务<br/>
	 * 该方法先把传入的scheduler赋值给内部属性scheduler，然后调用loadJobs()
	 * @param scheduler 调度器
	 */
	public void loadJobs(Scheduler scheduler) {
		this.scheduler = scheduler;
		loadJobs();
	}
	/**
	 * 重新加载Kettle任务<br/>
	 * 先把scheduler中组为 {@link KettleQuartzJob.KETTLE_JOB_GROUP} 的任务删除，然后调用loadJobs()
	 */
	public void reloadJobs(){
		try {
			Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(KettleQuartzJob.KETTLE_JOB_GROUP));
			if(jobKeys!=null){
				List<JobKey> jobKeysList = new ArrayList<JobKey>();
				jobKeysList.addAll(jobKeys);
				scheduler.deleteJobs(jobKeysList);
				logger.debug("在调度器中删除kettle任务集合", jobKeysList);
			}
		} catch (SchedulerException e) {
			logger.error("在调度器中删除kettle任务集合失败：", KettleQuartzJob.KETTLE_JOB_GROUP, e);
			throw new RuntimeException("在调度器中删除kettle任务集合失败："+KettleQuartzJob.KETTLE_JOB_GROUP, e);
		}
		loadJobs();
	}
	/**
	 * 加载kettle任务<br/>
	 * 该方法先调用loadKettleJobList()获取Kettle任务列表，然后逐条遍历，
	 * 如果状态为KettleJobBean.STATE_START，则调用addJob()方法，否则就跳过
	 */
	public void loadJobs(){
		// 读取数据库的任务配置
        List<KettleJobBean> jobList = loadKettleJobList();
        if(jobList==null || jobList.isEmpty()){
            logger.debug("未获取到Kettle Job列表，跳过。");
            return;
        }
        // 把任务放到调度器中
        for(KettleJobBean job : jobList){
            try {
                addJob(job);
            } catch (SchedulerException e) {
                logger.error("Kettle任务添加失败：", job, e);
            }
        }
	}
	/**
	 * 加载所有的Kettle Job
	 * @return 任务列表
	 */
    public List<KettleJobBean> loadKettleJobList(){
        logger.debug("获取Kettle Job列表");
        return kettleJobDao.selectAllJob();
    }
    /**
     * 加载Kettle Job的默认执行参数
     * @param jobId 任务ID
     * @return 参数及对应关系，key：参数名，value：参数值
     */
    public Map<String, String> loadKettleJobParameters(String jobId){
    	logger.debug("获取Kettle任务", jobId, "的默认参数");
    	Map<String, String> parameters = new HashMap<String, String>();
    	List<KettleJobParamBean> params = kettleJobParamsDao.selectParamsByJobId(jobId);
    	if(params!=null && !params.isEmpty()){
			for(KettleJobParamBean param : params){
				parameters.put(param.getParamCode(), param.getParamValue());
			}
    	}
    	logger.debug("获取Kettle任务", jobId, "的默认参数：", parameters);
        return parameters;
    }
    /**
     * 在调度中移除任务
     * @param job
     * @throws SchedulerException
     */
    public void removeJob(KettleJobBean job) throws SchedulerException{
    	JobKey key = new JobKey(job.getQuartzJobName(), KettleQuartzJob.KETTLE_JOB_GROUP);
    	if(scheduler.checkExists(key)){
    		scheduler.deleteJob(key);
    		logger.debug("在调度器中移除Kettle任务：", key);
    	}
    }
    /**
     * 在调度中新增任务，如果存在，则先删除
     * @param job
     * @throws SchedulerException
     */
    public void addJob(KettleJobBean job) throws SchedulerException{
    	if(!KettleJobBean.STATE_START.equals(job.getState())){
    		logger.debug("Kettle任务非启动状态，不添加到调度器中：", job);
    		return;
    	}
    	JobKey key = new JobKey(job.getQuartzJobName(), KettleQuartzJob.KETTLE_JOB_GROUP);
    	if(scheduler.checkExists(key)){
    		scheduler.deleteJob(key);
    		logger.debug("在调度器中移除Kettle任务：", key);
    	}
        // 创建jobDetail
        JobDetail jobDetail = JobBuilder.newJob(KettleQuartzJob.class)
        		.withIdentity(job.getQuartzJobName(), KettleQuartzJob.KETTLE_JOB_GROUP)
        		.withDescription(job.getDescription())
        		.build();
        // 把任务信息本身放到jobDetail里
        jobDetail.getJobDataMap().put(KettleQuartzJob.KETTLE_JOB_INFO, job);
        jobDetail.getJobDataMap().put(KettleQuartzJob.KETTLE_JOB_LOADER, this);
        jobDetail.getJobDataMap().put(KettleQuartzJob.KETTLE_JOB_RUNNER_FACTORY, kettleJobRunnerFactory);
        //时间调度
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getExp());
        // 创建trigger
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(job.getQuartzJobName(), KettleQuartzJob.KETTLE_JOB_GROUP).withSchedule(scheduleBuilder).build();
        scheduler.scheduleJob(jobDetail, trigger);
        logger.debug("在调度器中新增Kettle任务：", job);
    }
    
    public void updateJobRunInfo(){
    	
    }
	public String getScheduledState(String quartzJobName) {
		TriggerKey triggerKey = new TriggerKey(quartzJobName, KettleQuartzJob.KETTLE_JOB_GROUP);
		try {
			return scheduler.getTriggerState(triggerKey).name();
		} catch (SchedulerException e) {
			logger.error("获取任务调度状态时出错：", triggerKey, e);
			return "EXCEPTION";
		}
	}
	/**
	 * 暂停任务，被暂停任务将不再执行下一次，当前如果正在执行，将本次会执行完成。
	 * @param job
	 * @throws SchedulerException
	 */
	public void pauseJob(KettleJobBean job) throws SchedulerException {
		JobKey jobKey = new JobKey(job.getQuartzJobName(), KettleQuartzJob.KETTLE_JOB_GROUP);
		scheduler.pauseJob(jobKey);
	}
	/**
	 * 中断任务，任务当前如果正在执行，则强制停止执行
	 * @param job 要被中断的任务
	 * @throws UnableToInterruptJobException 不能中断任务时抛出异常
	 */
	public boolean interruptJob(KettleJobBean job) throws UnableToInterruptJobException {
		JobKey jobKey = new JobKey(job.getQuartzJobName(), KettleQuartzJob.KETTLE_JOB_GROUP);
		return scheduler.interrupt(jobKey);
	}
	
	public void resumeJob(KettleJobBean job) throws SchedulerException{
		JobKey jobKey = new JobKey(job.getQuartzJobName(), KettleQuartzJob.KETTLE_JOB_GROUP);
		scheduler.resumeJob(jobKey);
	}
	/**
	 * @return the scheduler
	 */
	public Scheduler getScheduler() {
		return scheduler;
	}
	/**
	 * @param scheduler the scheduler to set
	 */
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	/**
	 * @return the kettleJobRunnerFactory
	 */
	public KettleJobRunnerFactory getKettleJobRunnerFactory() {
		return kettleJobRunnerFactory;
	}
	/**
	 * @param kettleJobRunnerFactory the kettleJobRunnerFactory to set
	 */
	public void setKettleJobRunnerFactory(KettleJobRunnerFactory kettleJobRunnerFactory) {
		this.kettleJobRunnerFactory = kettleJobRunnerFactory;
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
}
