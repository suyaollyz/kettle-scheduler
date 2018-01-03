package cn.kettle.scheduler.scheduler;

import cn.kettle.scheduler.dao.KettleJobDao;
import cn.kettle.scheduler.dao.KettleJobRunLogDao;
import cn.kettle.scheduler.kettle.KettleJobRunner;

public class KettleJobRunnerFactory {
	private String runLogRootPath;
	KettleJobDao kettleJobDao;
	KettleJobRunLogDao kettleJobRunLogDao;
	
	public KettleJobRunner getRunner(){
		KettleJobRunner runner = new KettleJobRunner();
		runner.setKettleJobDao(kettleJobDao);
		runner.setKettleJobRunLogDao(kettleJobRunLogDao);
		runner.setRunLogRootPath(runLogRootPath);
		return runner;
	}

	/**
	 * @return the runLogRootPath
	 */
	public String getRunLogRootPath() {
		return runLogRootPath;
	}

	/**
	 * @param runLogRootPath the runLogRootPath to set
	 */
	public void setRunLogRootPath(String runLogRootPath) {
		this.runLogRootPath = runLogRootPath;
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
	 * @return the kettleJobRunLogDao
	 */
	public KettleJobRunLogDao getKettleJobRunLogDao() {
		return kettleJobRunLogDao;
	}

	/**
	 * @param kettleJobRunLogDao the kettleJobRunLogDao to set
	 */
	public void setKettleJobRunLogDao(KettleJobRunLogDao kettleJobRunLogDao) {
		this.kettleJobRunLogDao = kettleJobRunLogDao;
	}
	
}
