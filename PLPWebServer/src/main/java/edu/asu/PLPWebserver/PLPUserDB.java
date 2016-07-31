/**
 * 
 */
package edu.asu.PLPWebserver;

import java.util.HashMap;
import java.util.Random;


/**
 * @author ngoel2
 *
 */
public class PLPUserDB {
	
	private HashMap<Integer, UserSession> userSessionInfo;
	public static PLPUserDB instance = null;
	
	private PLPUserDB(){
		userSessionInfo = new HashMap<Integer, UserSession>();
	}
	
	/*
	 * 
	 */
	public int registerNewUser(String un){
		Random random = new Random();
		int sessionKey =  random.nextInt(Integer.MAX_VALUE);
		while (sessionKey >= 0 && userSessionInfo.containsKey(sessionKey)){
			sessionKey =  random.nextInt(Integer.MAX_VALUE);
		}
		if (sessionKey < 0){
			return -1;
		} else {
			UserSession user = new UserSession(un, sessionKey, System.currentTimeMillis());
			userSessionInfo.put(sessionKey, user);
			return sessionKey;
		}
	}
	
	/*
	 * 
	 */
	public UserSession getUser(int sessionKey){
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
