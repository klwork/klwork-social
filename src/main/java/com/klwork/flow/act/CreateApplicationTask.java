package com.klwork.flow.act;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import com.klwork.common.utils.StringTool;

public class CreateApplicationTask implements JavaDelegate {

	public void execute(DelegateExecution execution) {
		ActWorkApplication act = new ActWorkApplication();
		act.setCompanyName("test");
		String outsourcingProjectId = (String)execution.getVariable("outsourcingProjectId");
		String checker = (String)execution.getVariable("checker");
		if(StringTool.judgeBlank(checker)){
			execution.setVariable("goPreAudit", true);
		}else {
			execution.setVariable("goPreAudit", false);
		}
		
		/*act.setBounty((Long) execution.getVariable("bounty"));
		act.setCompanyName((String) execution.getVariable("companyName"));
		act.setAttachment((String) execution.getVariable("attachment"));
		act.setDescription((String) execution.getVariable("description"));*/
		/*la.setCreditCheckOk((Boolean) execution.getVariable("creditCheckOk"));
		*/
		execution.setVariable("actWorkApplication", act);
	}
}
