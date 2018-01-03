package cn.kettle.scheduler.kettle;

import java.util.Map;

import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;

import cn.kettle.scheduler.scheduler.KettleJobLoader;
import cn.kettle.scheduler.scheduler.KettleJobRunnerFactory;
import cn.kettle.scheduler.commons.util.Logger;

public class KettleQuartzJob implements InterruptableJob {
	private static Logger logger = new Logger();
	/** Kettle任务加载器 */
	public static final String KETTLE_JOB_LOADER="kettle_job_loader";
	/** Kettle任务运行器工厂 */
	public static final String KETTLE_JOB_RUNNER_FACTORY="kettle_job_runner_factory";
	/** Kettle任务原始对象数据 */
    public static final String KETTLE_JOB_INFO="kettle_job_info";
    /** Kettle任务组名 */
    public static final String KETTLE_JOB_GROUP = "KettleJobGroup";
    private KettleJobRunner kettleJobRunner;
    
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 读取任务信息
        KettleJobBean kettleJobInfo = (KettleJobBean)jobExecutionContext.getJobDetail().getJobDataMap().get(KETTLE_JOB_INFO);
        logger.debug("Kettle任务开始运行：", kettleJobInfo);
        // 获取任务加载器
        KettleJobLoader kettleJobLoader = (KettleJobLoader)jobExecutionContext.getJobDetail().getJobDataMap().get(KETTLE_JOB_LOADER);
        // 通过任务加载器读取数据库中任务配置的参数，并封装成map，key表示参数名，value表示参数值
        Map<String, String> parameters = kettleJobLoader.loadKettleJobParameters(kettleJobInfo.getJobId());
        // 获取任务运行器工厂
        KettleJobRunnerFactory kettleJobRunnerFactory = (KettleJobRunnerFactory)jobExecutionContext.getJobDetail().getJobDataMap().get(KETTLE_JOB_RUNNER_FACTORY);
        // 创建job，并执行
        kettleJobRunner = kettleJobRunnerFactory.getRunner();
        kettleJobRunner.start(kettleJobInfo, parameters);
    }

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		kettleJobRunner.stopKettleJob();
		logger.debug(kettleJobRunner+" is interrupted!");
	}
}
