package cn.kettle.scheduler.kettle;

import java.util.List;

import org.pentaho.di.core.KettleClientEnvironment;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.ProgressMonitorListener;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.RepositoryPluginType;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.RepositoriesMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryElementMetaInterface;
import org.pentaho.di.repository.RepositoryMeta;
import org.pentaho.di.repository.RepositoryObject;

import cn.kettle.scheduler.commons.util.Logger;

public class KettleUtil {
	private static Logger logger = new Logger();
	private static Repository repository;
	public static String repositoryName;
	public static Repository getRepository(){
		return repository;
	}
    public static void initRepository() throws KettleException {
    	logger.debug("Kettle资源库初始化开始");
        if(repository==null){
            initEnv();
            RepositoriesMeta repsinfo = new RepositoriesMeta();
        	repsinfo.readData();
        	RepositoryMeta repositoryMeta = repsinfo.findRepository(repositoryName);
        	if(repositoryMeta==null){
        		KettleException e = new KettleException("没有找到对应的资源库名称："+repositoryName);
        		logger.error(e);
        		throw e;
        	}
        	repository = PluginRegistry.getInstance().loadClass(RepositoryPluginType.class, repositoryMeta, Repository.class);
        	repository.init( repositoryMeta );
        	repository.loadRepositoryDirectoryTree();
            logger.info("Kettle资源库初始化成功，资源库名称：", repository.getName());
        }
        logger.debug("Kettle资源库初始化完成");
    }

    private static void initEnv() throws KettleException {
        if (System.getenv("KETTLE_HOME") != null) {
            System.setProperty("DI_HOME", System.getenv("KETTLE_HOME"));
            System.setProperty("KETTLE_HOME", System.getenv("KETTLE_HOME"));
            System.setProperty("org.osjava.sj.root", System.getenv("KETTLE_HOME") + "/simple-jndi");
            logger.info("KETTLE_HOME配置[能自动加载该目录下plugins中的插件]：" + System.getenv("KETTLE_HOME"));
        }

        if (System.getenv("KETTLE_JNDI_ROOT") != null) {
            System.setProperty("org.osjava.sj.root", System.getenv("KETTLE_JNDI_ROOT"));
            logger.info("Simple-jndi配置根路径：" + System.getenv("KETTLE_JNDI_ROOT"));
        }
        if (!KettleEnvironment.isInitialized()) {
            KettleEnvironment.init();
            KettleClientEnvironment.getInstance().setClient(KettleClientEnvironment.ClientType.KITCHEN);
        }

    }

    public static JobMeta loadJob(String jobname, String directory) throws KettleException {
        return loadJob(jobname, directory, repository);
    }

    public static JobMeta loadJob(String jobname, String directory, Repository repository) throws KettleException {
        RepositoryDirectoryInterface dir = repository.findDirectory(directory);
        JobMeta jobMeta = repository.loadJob(jobname, dir, (ProgressMonitorListener)null, (String)null);
        jobMeta.setRepository(repository);
        return jobMeta;
    }
    
    public static void printDirs(RepositoryDirectoryInterface directoryInterface, String indent){
        String nextIndent = " |"+indent;
        // print dir info
        logger.debug(indent+"-"+directoryInterface.getName());
        List<RepositoryDirectoryInterface> children = directoryInterface.getChildren();
        for(RepositoryDirectoryInterface child : children){
            printDirs(child, nextIndent);
        }

        // print transformations
        try {
            List<RepositoryElementMetaInterface> transObjects = repository.getTransformationObjects(directoryInterface.getObjectId(),false);
            for(RepositoryElementMetaInterface transObject : transObjects){
                logger.debug(nextIndent+"-"+((RepositoryObject)transObject).getName()+".ktr");
            }
        } catch (KettleException e) {
            logger.error(e);
        }
        // print jobs
        try {
            List<RepositoryElementMetaInterface> jobObjects = repository.getJobObjects(directoryInterface.getObjectId(),false);
            for(RepositoryElementMetaInterface jobObject : jobObjects){
                logger.debug(nextIndent+"-"+((RepositoryObject)jobObject).getName()+".kjb");
            }
        } catch (KettleException e) {
            logger.error(e);
        }
    }
}
