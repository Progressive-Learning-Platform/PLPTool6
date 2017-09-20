/**
 * 
 */
package edu.asu.plp.service;

import javax.servlet.http.HttpSession;

/**
 * @author ngoel2
 *
 */
public class UserSession {
	
	private String userName;
	private HttpSession session;
	private long userLastUsedTime;
	/**
	 * @param userName
	 * @param sessionKey
	 * @param userLastUsedTime
	 */
	public UserSession(String userName, HttpSession session, long userLastUsedTime) {
		super();
		this.userName = userName;
		this.session = session;
		this.userLastUsedTime = userLastUsedTime;
	}
	/**
	 * @return the userLastUsedTime
	 */
	public long getUserLastUsedTime() {
		return userLastUsedTime;
	}
	/**
	 * @param userLastUsedTime the userLastUsedTime to set
	 */
	public void setUserLastUsedTime(long userLastUsedTime) {
		this.userLastUsedTime = userLastUsedTime;
	}
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @return the userSessionKey
	 */
	public HttpSession getUserSession() {
		return session;
	}
	

}
