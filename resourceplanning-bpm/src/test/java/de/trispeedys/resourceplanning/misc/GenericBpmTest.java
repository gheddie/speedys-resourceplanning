package de.trispeedys.resourceplanning.misc;

import java.util.List;

import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.ProcessEngineRule;

public class GenericBpmTest
{
    protected boolean wasTaskGenerated(String taskId, ProcessEngineRule rule)
    {
        List<Task> list = rule.getTaskService().createTaskQuery().taskDefinitionKey(taskId).list();
        return ((list != null) && (list.size() > 0));
    }
}