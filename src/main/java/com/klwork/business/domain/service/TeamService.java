package com.klwork.business.domain.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.klwork.common.dto.vo.ViewPage;
import com.klwork.business.domain.model.Team;
import com.klwork.business.domain.model.TeamQuery;
import com.klwork.business.domain.repository.TeamRepository;


/**
 * 
 * @version 1.0
 * @created ${plugin.now}
 * @author ww
 */

@Service
public class TeamService {
	@Autowired
	private TeamRepository rep;

	public Team createTeam(Team team) {
		return null;
	}

	public void deleteTeam(Team team) {
	}

	public int updateTeam(Team team) {
		return 0;
	}

	public List<Team> findTeamByQueryCriteria(TeamQuery query,
			ViewPage<Team> page) {
		return rep.findTeamByQueryCriteria(query, page);
	}

	public Team findTeamById(long id) {
		return null;
	}

}