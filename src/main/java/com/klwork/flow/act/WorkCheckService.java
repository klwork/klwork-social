package com.klwork.flow.act;

import java.util.Date;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class WorkCheckService implements JavaDelegate {
	
	@Override
	public void execute(DelegateExecution execution) {
		/*System.out.println("execution id " + execution.getId());
		Long isbn = (Long) execution.getVariable("isbn");
		System.out.println("xx received isbn " + isbn);
		execution.setVariable("validatetime", new Date());*/
		//检查通过
		execution.setVariable("workChecked",true);
		System.out.println("检查通过");
		
	}
}
