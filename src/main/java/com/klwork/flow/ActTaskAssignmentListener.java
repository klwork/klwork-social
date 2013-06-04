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
package com.klwork.flow;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.identity.Authentication;

/**
 * @author Frederik Heremans
 */
public class ActTaskAssignmentListener implements TaskListener {

	private static final String CLAIM_USER_ID = "claimUserId";
	private static final long serialVersionUID = 1L;

	public void notify(DelegateTask delegateTask) {
		delegateTask.setDescription("TaskAssignmentListener is listening: "
				+ delegateTask.getAssignee());
		//从外部取
		Object userId = delegateTask.getVariable(CLAIM_USER_ID);
		if (userId != null) {
			System.out.println("外部claimUserId:" + userId);
			delegateTask.setAssignee((String) userId);
		} else {
			String authenticatedUserId = Authentication
					.getAuthenticatedUserId();
			delegateTask.setAssignee(authenticatedUserId);
			userId = authenticatedUserId;
		}
		saveAuthToVariable(delegateTask, userId.toString());
	}

	private void saveAuthToVariable(DelegateTask delegateTask,
			String authenticatedUserId) {
		delegateTask.setVariableLocal(CLAIM_USER_ID,
				authenticatedUserId);
		delegateTask.getExecution().setVariableLocal(CLAIM_USER_ID,
				authenticatedUserId);
	}
}
