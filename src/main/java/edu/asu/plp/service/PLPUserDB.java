/**
 * 
 */
package edu.asu.plp.service;

import java.util.HashMap;
import java.util.Random;

import javax.servlet.http.HttpSession;


/**
 * @author ngoel2
 *
 */
public class PLPUserDB {
	
	private HashMap<String, UserSession> userSessionInfo;
	public static PLPUserDB instance = null;
	
	private PLPUserDB(){
		userSessionInfo = new HashMap<String, UserSession>();
	}
	
	/*
	 * @brief: Creates a userSession object and inserts into the session map, which is later used to verify if the
	 * appropriate user has requested a functionality
	 * @params: un username
	 * @params: session object
	 * @params: String sessionKey
	 * @return: null
	 */
	public void registerUserSession(String un, HttpSession session, String sessionKey){
//		Random random = new Random();
//		int sessionKey =  random.nextInt(Integer.MAX_VALUE);
//		while (sessionKey >= 0 && userSessionInfo.containsKey(sessionKey)){
//			sessionKey =  random.nextInt(Integer.MAX_VALUE);
//		}
//		if (sessionKey < 0){
//			return -1;
//		} else {
			UserSession user = new UserSession(un, session, System.currentTimeMillis());
			userSessionInfo.put(sessionKey, user);
			//return sessionKey;
		//}
	}

	/*
	 * @brief: removes the user session object form the maintained map
	 * @params: String sessionKey
	 * @return: null
	 */
	public void removeUserSession(String sessionKey){
		if(userSessionInfo.containsKey(sessionKey)){
			userSessionInfo.remove(sessionKey);
		}
	}

	/*
	 * @brief: checks if a session is registered in the map
	 * @params: String sessionKey
	 * @ return: true if the User Session is present
	 */
	public boolean userSessionPresent(String sessionKey){
		return userSessionInfo.containsKey(sessionKey);
	}

	/*
	 * @brief: retrieves the User session object from the map if present
	 * @params: String sessionKey
	 * @return: User Session Object
	 */
	public UserSession getUser(String sessionKey){
		return userSessionInfo.get(sessionKey);
	}
	
	/*
	 * deprecated
	 */
	public String assembleCode(int sessionKey){
		// TODO: add all the relevant info required to assemble code
		// 			like file, etc.
		
		return "";
	}

	/*
	 * returns the singleton instance f PLPUserDB
	 */
	public static PLPUserDB getInstance(){
		if(instance == null){
			instance = new PLPUserDB();
		}
		return instance;
		
	}

}
