# kettle-scheduler
kettle目前推荐的调度方式是通过操作系统的任务计划来调度，当etl过程较多时，需要大量的调度脚本，这种方式导致操作复杂、管理困难，并且不能做到实时在线的监控控制。

在kettle使用过程中发现，kettle本身并没有版本管理的控制，当出现问题，需要回退时会非常麻烦。

因此，需要一个简洁的、无脚本的，同时又能实时监控到运行情况的调度平台。同时要考虑kettle文件如果进行版本的管理。

kettle-scheduler项目目的是使用JAVA程序调度Kettle的Job，不需要再编写大量调度脚本。同时可以实时在线看到Job的运行情况，并下载日志，便于分析运行过程中的问题，不需要再登录到服务器来获取日志。

kettle-scheduler目前支持Kettle资源库方式来存储Kettle Job。为解决版本管理问题，个人建议以文件资源库方式来存储，同时将文件资源库在SVN或者GIT等版本管理工具中进行管理，以便于对版本和问题进行控制。

kettle-scheduler本身不包含kettle的任何运行环境，因此需要开发和使用环境中先安装kettle环境。

1、目前程序只支持oracle数据库

2、数据库初始化脚本：src/main/resources/oracle_init.sql

3、程序数据连接配置文件：src/main/resources/jdbc.properties

4、pom.xml配置，需要将<kettle_home>路径修改成Kettle运行环境路径

5、本应用使用logback做为日志框架，应用日志配置文件：src/main/resources/logback.xml，默认应用日志与kettle运行日志路径相同

6、kettle任务运行日志输出路径配置：context.properties中的kettle_runLogRootPath

7、kettle资源库连接配置：context.properties中的repositoryName

7、tomcat需要进行的配置如下：
	
	1) 进入tomcat安装目录下的bin目录
	2) 在catalina.properties文件中找到common.loader所在行，在最后边添加Kettle lib目录下的所有jar包，例如：/home/kettle/pentaho-kettle-5.4.1.8-R/lib/*.jar
	3) 在catalina.properties最后添加Kettle的simple-jndi配置路径，例如：org.osjava.sj.root=/home/kettle/pentaho-kettle-5.4.1.8-R/simple-jndi

