package com.klwork.explorer.ui.task;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.task.Task;

public class TaskFormFactory {
	@SuppressWarnings("rawtypes")
	protected static Map<String, Class> factories = new HashMap<String, Class>();

	static {
		factories.put("crowdsourcing-publishNeed-form",
				com.klwork.flow.act.PublishNeedForm.class);
	}

	public static TaskForm create(String name, Task task ) {
		if(name.equals("crowdsourcing-publishNeed-form")){
			return new com.klwork.flow.act.PublishNeedForm(task);
		}
		return null;
		/*Class clazz = factories.get(name);
		try {
			TaskForm r = (TaskForm) clazz.newInstance();
			r.setTask(task);
			return r;
		} catch (Exception e) {
			throw new ActivitiException("Couldn't instantiate class " + clazz,
					e);
		}*/
	}
}
