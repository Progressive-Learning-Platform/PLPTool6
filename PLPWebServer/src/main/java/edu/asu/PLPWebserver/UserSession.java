/**
 * 
 */
package edu.asu.PLPWebserver;

/**
 * @author ngoel2
 *
 */
public class UserSession {
	
	private String userName;
	private int userSessionKey;
	private long userLastUsedTime;
	/**
	 * @param userName
	 * @param sessionKey
	 * @param userLastUsedTime
	 */
	public UserSession(String userName, int sessionKey, long userLastUsedTime) {
		super();
		this.userName = userName;
		this.userSessionKey = sessionKey;
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
	public int getUserSessionKey() {
		return userSessionKey;
	}
	

}
