package com.klwork.business.domain.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.klwork.business.domain.model.Project;
import com.klwork.business.domain.model.ProjectQuery;
import com.klwork.business.domain.model.Todo;
import com.klwork.business.domain.repository.ProjectRepository;
import com.klwork.business.domain.repository.TodoRepository;
import com.klwork.common.dto.vo.ViewPage;
import com.klwork.common.utils.StringDateUtil;

/**
 * 
 * @version 1.0
 * @created ${plugin.now}
 * @author ww
 */

@Service
public class ProjectService {
	@Autowired
	private ProjectRepository rep;
	
	@Autowired
	private TodoRepository todoRep;
	
	public void createProject(Project project) {
		project.setId(rep.getNextId());
		Date now = StringDateUtil.now();
		project.setCreationdate(now);
		project.setLastupdate(now);
		rep.insert(project);
		//默认在项目下创建一个todo
		Todo t = todoRep.newTodo();
		t.setProId(project.getId());
		todoRep.insert(t);
		
	}
	
	public void deleteProject(Project project) {
	}

	public int updateProject(Project project) {
		return 0;
	}

	public List<Project> findByQueryCriteria(ProjectQuery query,
			ViewPage<Project> page) {
		return rep.findProjectByQueryCriteria(query, page);
	}

	public Project findProjectById(String id) {
		return rep.find(id);
	}

	public int count(ProjectQuery query) {
		return rep.findProjectCountByQueryCriteria(query);
	}

	public void updateProjectName(String id, String name) {
		Project s = rep.find(id);
		s.setLastupdate(new Date());
		s.setName(name);
		rep.update(s);
	}
}