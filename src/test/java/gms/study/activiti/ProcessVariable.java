package gms.study.activiti;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:gms/spring-*.xml", "classpath*:gms/act*.xml" })
public class ProcessVariable {
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    /**
     * setValiable 与 setLocalValiable作用域不同
     */
    @Test
    public void deploeyFlow() {
        Deployment deploy = repositoryService.createDeployment()// 创建一个部署构建器
                .addClasspathResource("study/activiti/diagrams/ApplyBill.bpmn")// 从类路径一次只能添加一个文件
                .addClasspathResource("study/activiti/diagrams/ApplyBill.png").name("ApplyBill")// 设置流程名称
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
        String processDefinitionKey = "applyBill";

        // 取得流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey);
        
        System.out.println("流程(流程执行对象实例)id【" + processInstance.getId() + "】");// execution对象
        System.out.println("流程实例id:" + processInstance.getProcessInstanceId() + "】");// processInstance对象
        System.out.println("流程定义id【" + processInstance.getProcessDefinitionId() + "】");// 默认为最新的id
    }

    // 模拟流程变量
    @Test
    public void getAndSetProcessVariable() {
        // 两种服务可以设置流程变量

        String executionId = "";// 执行对象
        String variableName = "";// 变量名
        Object value = "";// 变量值
        /**
         * 1：通过runtimeService来设置流程变量
         */
        runtimeService.setVariable(executionId, variableName, value);

        // 设置本执行对象的变量，该作用域只在当前的executionId中
        runtimeService.setVariableLocal(executionId, variableName, value);

        Map<String, Object> variables = new HashMap<String, Object>();
        // 可以设置对个变量，放在map中
        runtimeService.setVariables(executionId, variables);

        /**
         * 2：通过taskService来设置流程变量
         */

        String taskId = "";
        taskService.setVariable(taskId, variableName, value);

        // 设置本执行对象的变量，该作用域只在当前的executionId中
        taskService.setVariableLocal(taskId, variableName, value);

        Map<String, Object> variableTasks = new HashMap<String, Object>();
        // 可以设置对个变量，放在map中
        taskService.setVariables(taskId, variableTasks);

        /**
         * 3：当流程开始执行的时候可以设置变量
         */
        String processDefinitionKey = "";// 流程定义的key
        runtimeService.startProcessInstanceByKey(processDefinitionKey, variables);

        /**
         * 4:当任务执行的时候设置流程变量
         */
        taskService.complete(taskId, variables);

        /**
         * 5:通过runtimeService取变量值
         */

        runtimeService.getVariable(executionId, variableName);
        runtimeService.getVariableLocal(executionId, variableName);
        runtimeService.getVariables(executionId);

        /**
         * 6:通过taskService 取变量值
         */

        taskService.getVariable(taskId, variableName);
        taskService.getVariableLocal(taskId, variableName);
        taskService.getVariables(taskId);
    }

    /**
     * 设置流程变量值
     */
    @Test
    public void setVariables() {
        String taskId = "45004";
        String variableName = "cost";
        int value = 10000;
        // 采用taskService来设置变量值（较多）
       //taskService.setVariable(taskId, variableName, value);
       // taskService.setVariableLocal(taskId, "申请时间", new Date());//该任务只在当前任务中有效
        taskService.setVariableLocal(taskId, "申请人", "某某");//再次设置会覆盖

        System.out.println("设置成功");

    }
    
    /**
     * 查询任务变量
     */
    @Test
    public void getVariables() {
        String taskId = "45004";
        //在本地中进行获取可以获取
        Integer variable = (Integer) taskService.getVariable(taskId, "cost");
        String applyUser = (String) taskService.getVariableLocal(taskId, "申请人");
        Date applyTime = (Date) taskService.getVariable(taskId, "申请时间");

        //全局的变量可以在本地取不到，本地变量在全局中可以取得
        System.out.println("金额【"+variable+"】申请时间【"+applyTime+"】申请人【"+applyUser+"】");
       

    }
    

}
