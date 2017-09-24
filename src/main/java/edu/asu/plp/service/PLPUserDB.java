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
	 * 
	 */
	public void registerNewUser(String un, HttpSession session, String sessionKey){
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
	 * 
	 */
	public UserSession getUser(String sessionKey){
		return userSessionInfo.get(sessionKey);
		
	}
	
	/*
	 * 
	 */
	public String assembleCode(int sessionKey){
		// TODO: add all the relevant info required to assemble code
		// 			like file, etc.
		
		return "";
	}

	/*
	 * 
	 */
	public static PLPUserDB getInstance(){
		if(instance == null){
			instance = new PLPUserDB();
			
		}
		return instance;
		
	}

}
