package cn.kettle.scheduler.kettle;

import org.pentaho.di.core.exception.KettleException;

public class KettleRepository {
	private String repositoryName;
	public void initRepository() throws KettleException{
		KettleUtil.repositoryName=repositoryName;
		KettleUtil.initRepository();
	}
    /**
	 * @return the repositoryName
	 */
	public String getRepositoryName() {
		return repositoryName;
	}
	/**
	 * @param repositoryName the repositoryName to set
	 */
	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}
}
