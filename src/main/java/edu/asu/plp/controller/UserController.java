package edu.asu.plp.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.asu.plp.model.UserModel;
import edu.asu.plp.service.UserService;

@RestController
public class UserController {
	
	@Autowired
	UserService userService;
	
	@RequestMapping("/user")
	public Principal userLogin(Principal principal){
		return principal;
	}
	
	@RequestMapping(value="/saveUser", method=RequestMethod.POST)
	public void saveUser(@RequestBody UserModel user){
		
		userService.saveUser(user.email, user.firstName, user.lastName);
	}
}
