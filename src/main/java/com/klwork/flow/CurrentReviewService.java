package com.klwork.flow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class CurrentReviewService implements JavaDelegate {
	
	@Override
	public void execute(DelegateExecution execution) {
		//String participants = (String) execution.getVariable("participants");
		String participants  = "ww_management,ww";
		//找到任务的审核人进行审核
		String[] participantsArray = participants.split(",");
		List<String> assigneeList = new ArrayList<String>();
		for (String assignee : participantsArray) {
			assigneeList.add(assignee);
		}
		execution.setVariable("reviewersList", assigneeList);
	}
}
