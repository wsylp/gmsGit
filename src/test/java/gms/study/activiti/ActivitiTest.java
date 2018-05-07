package gms.study.activiti;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.junit.Test;

public class ActivitiTest {

    /** 使用代码创建工作流需要的23张表 */
    @Test
    public void createTable() {
        // 流程引擎ProcessEngine对象，所有操作都离不开引擎对象
        ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration
                .createStandaloneInMemProcessEngineConfiguration();
        // 连接数据库的配置
        processEngineConfiguration.setJdbcDriver("com.mysql.jdbc.Driver");
        processEngineConfiguration.setJdbcUrl(
                "jdbc:mysql://192.168.2.163:3306/activiti?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=utf8");
        processEngineConfiguration.setJdbcUsername("root");
        processEngineConfiguration.setJdbcPassword("root");

        // 三个配置
        // 1.先删除表，再创建表：processEngineConfiguration.DB_SCHEMA_UPDATE_CREATE_DROP="create-drop"
        // 2.不能自动创建表，需要表存在：processEngineConfiguration.DB_SCHEMA_UPDATE_FALSE="false"
        // 3.如果表存在，就自动创建表：processEngineConfiguration.DB_SCHEMA_UPDATE_TRUE="true"
        processEngineConfiguration.setDatabaseSchema(processEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        // 获取工作流的核心对象，ProcessEngine对象
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
        System.out.println("processEngine:" + processEngine + "Create Success!!");
    }

    @Test
    public void createtableByXml() {
        ProcessEngineConfiguration engineConfiguration = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("gms/activiti.cfg.xml");
        ProcessEngine buildProcessEngine = engineConfiguration.buildProcessEngine();
        System.out.println("创建成功");
    }

    @Test
    public void createtableBDefault() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    }

    @Test
    public void deployActiviti() {
        ProcessEngineConfiguration engineConfiguration = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("gms/activiti.cfg.xml");
        //取得流程引擎对象
        ProcessEngine processEngine = engineConfiguration.buildProcessEngine();
        //仓库服务
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment()//创建一个部署构建器
        .addClasspathResource("study/activiti/diagrams/LeaveBill.bpmn")//从类路径一次只能添加一个文件
        .addClasspathResource("study/activiti/diagrams/LeaveBill.png")
        .name("LeaveBill")//设置流程名称
        .category("办公流程")//设置流程类别
        .deploy();
        
        System.out.println("名称：【"+deploy.getName()+"】id【"+deploy.getId()+"】类别【"+deploy.getCategory()+"】");
    }

}
