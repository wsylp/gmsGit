package gms.study.activiti;

import java.util.List;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
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

public class ProcessInstanceAndTask {
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;
    
    @Test
    public void startProcess() {
        // 取服务
        String processDefinitionKey = "BuyBill";

        // 取得流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey);

        System.out.println("流程(流程执行对象实例)id【" + processInstance.getId() + "】");// execution对象
        System.out.println("流程实例id:" + processInstance.getProcessInstanceId() + "】");// processInstance对象
        System.out.println("流程定义id【" + processInstance.getProcessDefinitionId() + "】");// 默认为最新的id
    }

    /**
     * 查询任务
     * 
     */
    @Test
    public void getTasks() {
        // 25004 20004 17504 22504

        TaskQuery taskQuery = taskService.createTaskQuery();
        // 任务列表
        List<Task> list = taskQuery.list();
        // taskService.complete("37504");

        //
        for (Task task : list) {
            System.out.print("任务处理人【" + task.getAssignee() + "】");
            System.out.print("流程名称【" + task.getName() + "】");
            System.out.print("任务id【" + task.getId() + "】");
            System.out.println("流程定义id【" + task.getProcessDefinitionId() + "】");
        }

    }

    // 获取流程状态
    @Test
    public void getProcessInstanceState() {
        String processInstanceId = "37501";
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
                .singleResult();// 返回数据结果，要么单行，要么是空，其他情况报错;

        if (pi != null) {
            System.out.println("该流程实例" + processInstanceId + "正在运行.....");
            System.out.println("当前活动的任务" + pi.getActivityId());

        } else {
            System.out.println("该任务已经结束。。。");
        }
    }
    
    /**
     * 查看历史执行流程实例信息
     */
    @Test
    public void queryHistoryProcinst() {
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery()
        .list();
        if(list != null && list.size() >0) {
            for (HistoricProcessInstance m : list) {
                System.out.print("历史的流程实例" + m.getId());
                System.out.print("历史流程定义id" + m.getProcessDefinitionId());
                System.out.println("历史流程实例开始时间---结束时间:" + m.getStartTime()+"--->" + m.getEndTime());
            }
        }
        
    }
    
    
    /**
     * 查看历史执行流程任务信息
     */
    @Test
    public void queryHistoryTask() {
         String processInstanceId = "17501";
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                 .processInstanceId(processInstanceId )
        .list();
        if(list != null && list.size() >0) {
            for (HistoricTaskInstance m : list) {
                System.out.print("历史的流程任务" + m.getId());
                System.out.print("历史流程定义id" + m.getProcessDefinitionId());
                System.out.print("历史任务名称" + m.getName());
                System.out.println("历史任务实例处理人:" + m.getAssignee());
            }
        }
        
    }
}
