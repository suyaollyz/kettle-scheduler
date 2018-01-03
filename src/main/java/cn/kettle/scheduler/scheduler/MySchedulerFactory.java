package cn.kettle.scheduler.scheduler;

import java.util.ArrayList;
import java.util.List;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

public class MySchedulerFactory {
	SchedulerFactory schedulerFactory = null;
    Scheduler scheduler = null;
    List<JobLoader> jobLoaders = new ArrayList<JobLoader>();
    public MySchedulerFactory() throws SchedulerException {
    	schedulerFactory = new StdSchedulerFactory();
        scheduler = schedulerFactory.getScheduler();
        scheduler.start();
    }
    public void loadJobs(){
    	for(JobLoader jobLoader : jobLoaders){
    		jobLoader.loadJobs(scheduler);
    	}
    }
	/**
	 * @return the sf
	 */
	public SchedulerFactory getSchedulerFactory() {
		return schedulerFactory;
	}
	/**
	 * @param sf the sf to set
	 */
	public void setSf(SchedulerFactory schedulerFactory) {
		this.schedulerFactory = schedulerFactory;
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
	 * @return the jobLoaders
	 */
	public List<JobLoader> getJobLoaders() {
		return jobLoaders;
	}
	/**
	 * @param jobLoaders the jobLoaders to set
	 */
	public void setJobLoaders(List<JobLoader> jobLoaders) {
		this.jobLoaders = jobLoaders;
	}
    
}
