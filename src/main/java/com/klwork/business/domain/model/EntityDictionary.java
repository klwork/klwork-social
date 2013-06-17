package com.klwork.business.domain.model;

public interface EntityDictionary {

	// 新建
	static final String OUTSOURCING_STATUS_NEW = "0";
	
	// 需求正在审核
		static final String OUTSOURCING_STATUS_PUBLISHING = "1";

	// 需求已经发布
	static final String OUTSOURCING_STATUS_PUBLISHED = "2";
	
	
	//扩展的流程或任务的人员类型
	public static final String IDENTITY_LINK_TYPE_AUDITOR = "auditor";//审核人
}
