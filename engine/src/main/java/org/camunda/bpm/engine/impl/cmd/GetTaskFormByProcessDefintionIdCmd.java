/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.camunda.bpm.engine.impl.cmd;

import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.form.handler.TaskFormHandler;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.impl.task.TaskDefinition;

import java.io.Serializable;

import static org.camunda.bpm.engine.impl.util.EnsureUtil.ensureNotNull;


/**
 * @author Hagen Jung
 */
public class GetTaskFormByProcessDefintionIdCmd implements Command<TaskFormData>, Serializable {

    protected String taskDefinitionId;
    protected String processDefinitionId;
    protected ExecutionEntity executionEntity;

    public GetTaskFormByProcessDefintionIdCmd(String processDefintionId, String taskDefinitionId, ExecutionEntity executionEntity ) {
        this.taskDefinitionId = taskDefinitionId;
        this.processDefinitionId = processDefintionId;
        this.executionEntity = executionEntity;
    }

  public TaskFormData execute(CommandContext commandContext) {

      ProcessDefinitionEntity processDefinition = Context
              .getProcessEngineConfiguration()
              .getDeploymentCache()
              .findDeployedLatestProcessDefinitionByKey(processDefinitionId);
      ensureNotNull("No deployed process definition found for id '" + processDefinitionId + "'", "process definition", processDefinition);

      TaskDefinition taskDefinition = null;
      TaskFormData taskFormData= null;
      try {
           taskDefinition = processDefinition
            .getTaskDefinitions()
            .get(taskDefinitionId);
      } catch (NullPointerException npe) {
          // Standalone task, no TaskFormData available
      } finally {
          if (taskDefinition != null) {
              TaskFormHandler taskFormHandler = taskDefinition.getTaskFormHandler();
              ensureNotNull("No taskFormHandler specified for task definition '" + taskDefinitionId + "'", "taskFormHandler", taskFormHandler);

              ensureNotNull("No execution entity in context found", "execution entity", executionEntity);
              taskFormData = taskFormHandler.createFormOnExecutionByTaskDefinition(taskDefinition, executionEntity);
          }
      }
      return taskFormData;
  }
}