package com.klwork.flow;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class CreateApplicationTask implements JavaDelegate {

	public void execute(DelegateExecution execution) {
		ActWorkApplication act = new ActWorkApplication();
		act.setBounty((Long) execution.getVariable("bounty"));
		act.setCompanyName((String) execution.getVariable("companyName"));
		act.setAttachment((String) execution.getVariable("attachment"));
		act.setDescription((String) execution.getVariable("description"));
		/*la.setCreditCheckOk((Boolean) execution.getVariable("creditCheckOk"));
		*/
		execution.setVariable("actWorkApplication", act);
	}
}
