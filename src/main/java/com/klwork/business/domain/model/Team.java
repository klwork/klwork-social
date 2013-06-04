package com.klwork.business.domain.model;


import java.io.Serializable;

/**
 * 
 * @version 1.0
 * @created ${plugin.now}
 * @author ww
 * 
 */
public class Team implements Serializable 
 {
 	private static final long serialVersionUID = 1L;
 	public Team() {
 	}
 

	/**
	 *  
	 */
	private String id;
	/**
	 *  
	 */
	private String ownuser;

	/**
	 * 
	 *
	 * @param id
	 */
	public void setId(String id){
		this.id = id;
	}
	/**
	 * 
	 *
	 * @return
	 */	
	public String getId(){
		return id;
	}
	/**
	 * 
	 *
	 * @param ownuser
	 */
	public void setOwnuser(String ownuser){
		this.ownuser = ownuser;
	}
	/**
	 * 
	 *
	 * @return
	 */	
	public String getOwnuser(){
		return ownuser;
	}
}