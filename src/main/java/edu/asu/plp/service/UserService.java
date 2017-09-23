package edu.asu.plp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.plp.dao.UserDao;

@Service
public class UserService {
	
	@Autowired
	UserDao userDao;
	
	public void saveUser(String emailId, String firstName, String lastName){
		userDao.saveUser(emailId, firstName, lastName);
	}

}
