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
package org.camunda.bpm.engine.impl.incident;

import java.util.List;

import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.persistence.entity.IncidentEntity;
import org.camunda.bpm.engine.runtime.Incident;

public class FailedJobIncidentHandler implements IncidentHandler {

  public final static String INCIDENT_HANDLER_TYPE = "failedJob";
  
  public String getIncidentHandlerType() {
    return INCIDENT_HANDLER_TYPE;
  }

  public void handleIncident(String processDefinitionId, String activityId, String executionId, String jobId) {
    
    if(executionId != null) {
      IncidentEntity newIncident = IncidentEntity.createAndInsertIncident(INCIDENT_HANDLER_TYPE, executionId, jobId);
      newIncident.createRecursiveIncidents();
      
    } else {      
      IncidentEntity.createAndInsertIncident(INCIDENT_HANDLER_TYPE, processDefinitionId, activityId, jobId);
      
    }
    
  }
  
  public void resolveIncident(String processDefinitionId, String activityId, String executionId, String jobId) {

    List<Incident> incidents = Context
        .getCommandContext()
        .getIncidentManager()
        .findIncidentByConfiguration(jobId);
    
    for (Incident currentIncident : incidents) {
      IncidentEntity incident = (IncidentEntity) currentIncident;
      incident.delete();
    }
        
  }

}