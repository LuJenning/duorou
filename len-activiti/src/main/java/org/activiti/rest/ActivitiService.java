package org.activiti.rest;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.image.HMProcessDiagramGenerator;
import org.activiti.rest.model.ActivitiProcess;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhuxiaomeng
 * @date 2019-03-10.
 * @email 154040976@qq.com
 */
@Service
public class ActivitiService {

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    @Autowired
    IdentityService identityService;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    ProcessEngineFactoryBean processEngine;

    @Autowired
    ProcessEngineConfiguration processEngineConfiguration;

    @Autowired
    HistoryService historyService;

    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-hh mm:ss");

    /**
     * 获取需要高亮的线
     *
     * @param processDefinitionEntity
     * @param historicActivityInstances
     * @return
     */
    private List<String> getHighLightedFlows(
            ProcessDefinitionEntity processDefinitionEntity,
            List<HistoricActivityInstance> historicActivityInstances) {

        List<String> highFlows = new ArrayList<String>();

        for (int i = 0; i < historicActivityInstances.size() - 1; i++) {
            ActivityImpl activityImpl = processDefinitionEntity
                    .findActivity(historicActivityInstances.get(i)
                            .getActivityId());
            List<ActivityImpl> sameStartTimeNodes = new ArrayList<ActivityImpl>();
            ActivityImpl sameActivityImpl1 = processDefinitionEntity
                    .findActivity(historicActivityInstances.get(i + 1)
                            .getActivityId());
            sameStartTimeNodes.add(sameActivityImpl1);

            for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
                HistoricActivityInstance activityImpl1 = historicActivityInstances
                        .get(j);
                HistoricActivityInstance activityImpl2 = historicActivityInstances
                        .get(j + 1);

                if (activityImpl1.getStartTime().equals(
                        activityImpl2.getStartTime())) {

                    ActivityImpl sameActivityImpl2 = processDefinitionEntity
                            .findActivity(activityImpl2.getActivityId());
                    sameStartTimeNodes.add(sameActivityImpl2);

                } else {
                    break;
                }
            }
            List<PvmTransition> pvmTransitions = activityImpl
                    .getOutgoingTransitions();
            for (PvmTransition pvmTransition : pvmTransitions) {
                ActivityImpl pvmActivityImpl = (ActivityImpl) pvmTransition
                        .getDestination();
                if (sameStartTimeNodes.contains(pvmActivityImpl)) {
                    Map<String, Object> properties = pvmActivityImpl.getProperties();
                    System.out.println(properties);
                    highFlows.add(pvmTransition.getId());
                }
            }
        }
        return highFlows;
    }

    public List<ActivitiProcess> getTaskSqu(String processInstanceId) {
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId)
                .list();
        List<ActivitiProcess> activitiProcesses = new ArrayList<>();

        list.forEach(s -> {
            String assignee = s.getAssignee();
            ActivitiProcess activitiProcess = new ActivitiProcess();
            //组
            List<HistoricIdentityLink> historicIdentityLinksForTask = historyService.getHistoricIdentityLinksForTask(s.getId());
            List<String> groupName = new ArrayList<>();
            historicIdentityLinksForTask.forEach(hist -> {
                if(!StringUtils.isEmpty(hist.getGroupId())){
                    List<Group> groupList = identityService.createGroupQuery().groupId(hist.getGroupId()).list();
                    if (groupList.size() > 0) {
                        List<String> groupNames = groupList.stream().map(Group::getName).collect(Collectors.toList());
                        groupName.addAll(groupNames);
                    }
                }
            });
            activitiProcess.setGroupNames(groupName);
            if (!StringUtils.isEmpty(assignee)) {
                activitiProcess.setUserId(assignee);
                User user = identityService.createUserQuery().userId(assignee).singleResult();
                if(user!=null){
                    activitiProcess.setUserName(user.getFirstName());
                }
                activitiProcess.setSid(s.getTaskDefinitionKey());
            }

            activitiProcess.setTaskName(s.getName());
            if(s.getEndTime()!=null){
                activitiProcess.setTime(simpleDateFormat.format(s.getEndTime()));
            }
            activitiProcesses.add(activitiProcess);
        });
        return activitiProcesses;
    }

    public InputStream generateStream(String processInstanceId, boolean needCurrent) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        HistoricProcessInstance historicProcessInstance =
                historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        String processDefinitionId = null;
        List<String> executedActivityIdList = new ArrayList<String>();
        List<String> currentActivityIdList = new ArrayList<>();
        List<HistoricActivityInstance> historicActivityInstanceList = new ArrayList<>();
        if (processInstance != null) {
            processDefinitionId = processInstance.getProcessDefinitionId();
            if (needCurrent) {
                currentActivityIdList = this.runtimeService.getActiveActivityIds(processInstance.getId());
            }
        }
        if (historicProcessInstance != null) {
            processDefinitionId = historicProcessInstance.getProcessDefinitionId();
            historicActivityInstanceList =
                    historyService.createHistoricActivityInstanceQuery().finished().processInstanceId(processInstanceId).orderByHistoricActivityInstanceId().asc().list();
            for (HistoricActivityInstance activityInstance : historicActivityInstanceList) {
                executedActivityIdList.add(activityInstance.getActivityId());
            }
        }

        if (StringUtils.isEmpty(processDefinitionId) || executedActivityIdList.isEmpty()) {
            return null;
        }


        //高亮线路id集合
        ProcessDefinitionEntity definitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);
        List<String> highLightedFlows = getHighLightedFlows(definitionEntity, historicActivityInstanceList);

        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        //List<String> activeActivityIds = runtimeService.getActiveActivityIds(processInstanceId);
        processEngineConfiguration = processEngine.getProcessEngineConfiguration();
        Context.setProcessEngineConfiguration((ProcessEngineConfigurationImpl) processEngineConfiguration);
        HMProcessDiagramGenerator diagramGenerator = (HMProcessDiagramGenerator) processEngineConfiguration.getProcessDiagramGenerator();
        //List<String> activeIds = this.runtimeService.getActiveActivityIds(processInstance.getId());

        InputStream imageStream = diagramGenerator.generateDiagram(
                bpmnModel, "png",
                executedActivityIdList, highLightedFlows,
                processEngine.getProcessEngineConfiguration().getActivityFontName(),
                processEngine.getProcessEngineConfiguration().getLabelFontName(),
                "宋体",
                null, 1.0, currentActivityIdList);

        return imageStream;
    }
}
