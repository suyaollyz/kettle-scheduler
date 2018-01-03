package cn.kettle.scheduler.scheduler;

import org.quartz.Scheduler;

public interface JobLoader {
	/**
	 * 加载任务对象
	 * @param scheduler 调试器
	 */
	public void loadJobs(Scheduler scheduler);
}
