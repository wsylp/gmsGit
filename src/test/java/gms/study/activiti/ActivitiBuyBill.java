package gms.study.activiti;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:gms/spring-*.xml", "classpath*:gms/act*.xml" })
public class ActivitiBuyBill {

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
    public void deployFlowZip() {
        InputStream in = this.getClass().getClassLoader()
                .getResourceAsStream("study/activiti/diagrams/zip/BuyBill.zip");
        ZipInputStream zipInputStream = new ZipInputStream(in);
        Deployment deploy = repositoryService.createDeployment()// 创建一个部署构建器
                .addZipInputStream(zipInputStream).name("采购流程").category("办公流程")// 设置流程类别
                .deploy();

        System.out.println("名称：【" + deploy.getName() + "】id【" + deploy.getId() + "】类别【" + deploy.getCategory() + "】");
    }

    /**
     * 查询流程定义
     * 
     */
    @Test
    public void queryProcessDefin() {
        String processDefinitionKey = "BuyBill";
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                // .processDefinitionId(processDefinitionId)//流程定义id
                // BuyBill:1:10004 组成，proDefiKey(流程定义的key) + version(版本) + 自动生成的id
                .processDefinitionKey(processDefinitionKey)// 流程定义的key
                // .processDefinitionName(processDefinitionName)//流程定义名称
                // .processDefinitionVersion(processDefinitionVersion)//流程定义版本
                // .latestVersion()//最新版本
                // .orderByProcessDefinitionName().desc()//安装版本降序排序
                // .count()//统计结果
                // .listPage(firstResult, maxResults)//分页查询
                .list();
        // 遍历结果
        for (ProcessDefinition processDefinition : list) {
            System.out.print("流程定义id" + processDefinition.getId());
            System.out.print("流程定义的key" + processDefinition.getKey());
            System.out.print("流程部署id" + processDefinition.getDeploymentId());
            System.out.println("流程定义的版本" + processDefinition.getVersion());

        }
    }

    /**
     * 查看资源图片
     * 
     * @throws IOException
     */
    @Test
    public void viewImage() throws IOException {
        String deploymentId = "15001";
        // 取得某个部署资源的名称 deploymentId
        List<String> resourceNames = repositoryService.getDeploymentResourceNames(deploymentId);
        String resourceName = "";
        for (String m : resourceNames) {
            if (m.endsWith(".png")) {
                resourceName = m;
                System.out.println(m);
            }
        }

        /**
         * 读取资源
         * 
         * @params deploymentId 部署id
         * @params resourceName 资源文件名
         */
        InputStream resourceAsStream = repositoryService.getResourceAsStream(deploymentId, resourceName);
        // 把流写入到文件中
        String pathName = "E:/" + resourceName;
        File file = new File(pathName);
        FileUtils.copyInputStreamToFile(resourceAsStream, file);
        System.out.println("输出完成");
    }

    
    /**
     * 删除流程定义
     * 
     */
    @Test
    public void deleteProcessDefine() {
        //通过部署流程id来删除
        String deploymentId = "15001";
        repositoryService.deleteDeployment(deploymentId);
        System.out.println("删除成功");
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

        System.out.println("流程(流程实例)id【" + processInstance.getId() + "】流程定义id【"
                + processInstance.getProcessDefinitionId() + "】流程部署id【" + processInstance.getDeploymentId() + "】");
    }

    /**
     * 查询任务
     * 
     */
    @Test
    public void getTasks() {
        // 任务办理人
        // String assignee = "张三";
        String assignee = "李四";
        TaskQuery taskQuery = taskService.createTaskQuery();
        // 任务列表
        List<Task> list = taskQuery.taskAssignee(assignee)// 指定办理人
                .list();

        for (Task task : list) {
            System.out.println("任务处理人【" + task.getAssignee() + "】流程名称【" + task.getName() + "】任务id【" + task.getId()
                    + "】流程定义id【" + task.getProcessDefinitionId() + "】");
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
