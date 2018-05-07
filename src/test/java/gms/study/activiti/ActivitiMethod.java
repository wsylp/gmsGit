package gms.study.activiti;

import java.util.List;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:gms/spring-*.xml", "classpath*:gms/act*.xml" })
public class ActivitiMethod {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private TaskService taskService;

    /**
     * 发布流程，发布后数据会保存到数据库中
     * 
     * 部署影响的表：ACT_RE_PROCDEF,ACT_RE_DEPLOYMENT,ACT_GE_PROPERTY,重新部署也会影响
     * ACT_RE_PROCDEF 流程定义表： 该表的key属性是bpmn的id决定 该表的那么属性 是bpmn的name属性
     * ACT_RE_DEPLOYMENT 部署表 id是ACT_GE_PROPERTY的next_dbid决定
     * 
     * ACT_GE_PROPERTY 属性表
     */
    @Test
    public void deploeyFlow() {
        Deployment deploy = repositoryService.createDeployment()// 创建一个部署构建器
                .addClasspathResource("study/activiti/diagrams/BuyBill.bpmn")// 从类路径一次只能添加一个文件
                .addClasspathResource("study/activiti/diagrams/BuyBill.png").name("BuyBill")// 设置流程名称
                .category("办公流程")// 设置流程类别
                .deploy();

        System.out.println("名称：【" + deploy.getName() + "】id【" + deploy.getId() + "】类别【" + deploy.getCategory() + "】");
    }

    /**
     * 启动/执行流程
     * 
     */
    @Test
    public void startProcess() {
        // 取服务
        String processDefinitionKey = "leaveBill";

        // 取得流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey);

        System.out.println("流程(流程实例)id【" + processInstance.getId() + "】流程定义id【" + processInstance.getProcessDefinitionId()
                + "】流程部署id【" + processInstance.getDeploymentId() + "】");
    }
    
    /**
     * 查询任务
     * 
     */
    @Test
    public void getTasks() {
        //任务办理人
//        String assignee = "张三";
         String assignee = "李四";
        TaskQuery taskQuery = taskService.createTaskQuery();
        //任务列表
        List<Task> list = taskQuery.taskAssignee(assignee)//指定办理人
                        .list();
        
        for (Task task : list) {
            System.out.println("任务处理人【"+task.getAssignee()+"】流程名称【"+task.getName()+"】任务id【"+task.getId()+"】流程定义id【"+task.getProcessDefinitionId()+"】");
        }
        
    }
    
    /**
     * 完成任务
     */
    @Test
    public void finshTask() {
        String taskId = "5004";
        taskService.complete(taskId);
        System.out.println("完成任务");
    }
}
