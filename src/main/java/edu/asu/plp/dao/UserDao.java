package edu.asu.plp.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {
	String query;
	
	@Autowired
	JdbcTemplate jdbc;
	
	public void saveUser(String emailId, String firstName, String lastName) {
		query = "insert into user_info (email_id, first_name, last_name) values (?,?,?) on duplicate key update first_name=?, last_name=? ";
		jdbc.update(query, new Object[]{emailId,firstName,lastName,firstName,lastName});
	}

}
