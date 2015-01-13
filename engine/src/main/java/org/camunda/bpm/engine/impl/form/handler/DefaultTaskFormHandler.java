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

package org.camunda.bpm.engine.impl.form.handler;

import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.form.FormProperty;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.el.ExpressionManager;
import org.camunda.bpm.engine.impl.form.TaskFormDataImpl;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.impl.task.TaskDefinition;
import org.camunda.bpm.engine.impl.util.xml.Element;

/**
 * @author Tom Baeyens
 *
 */
public class DefaultTaskFormHandler extends DefaultFormHandler implements TaskFormHandler {

    /**
     * creates the task form data at runtime
     * */

    @Override
    public TaskFormData createTaskForm(TaskEntity task, ExecutionEntity executionEntity) {
        TaskFormData taskFormData = new TaskFormDataImpl();

        if(task!=null){
            Expression formKey = task.getTaskDefinition().getFormKey();

            if (formKey != null) {
                Object formValue = formKey.getValue(task);
                if (formValue != null) {
                    taskFormData.setFormKey(formValue.toString());
                }
            }
            taskFormData.setDeploymentId(deploymentId);
            taskFormData.setTask(task);
            initializeFormProperties(taskFormData, executionEntity);
        }
        initializeFormFields(taskFormData, executionEntity);
        return taskFormData;
    }

    @Override
    public TaskFormData createFormOnExecutionByTaskDefinition(TaskDefinition taskDefinition, ExecutionEntity executionEntity) {
        TaskFormData taskFormData = new TaskFormDataImpl();

        if(taskDefinition!=null){
            taskFormData.setDeploymentId(deploymentId);
            taskFormData.setTask(null);
        }
        initializeFormFields(taskFormData, executionEntity);
        return taskFormData;
    }
}
