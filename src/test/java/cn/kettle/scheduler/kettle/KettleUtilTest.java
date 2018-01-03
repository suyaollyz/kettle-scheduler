package cn.kettle.scheduler.kettle;

import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.Repository;

import cn.kettle.scheduler.kettle.KettleUtil;

public class KettleUtilTest {
	@Test
	public void printDirsTest() throws KettleException{
		KettleUtil.repositoryName="kettle";
		KettleUtil.initRepository();
		Repository repository = KettleUtil.getRepository();
		KettleUtil.printDirs(repository.findDirectory("/"), "");
	}
}
