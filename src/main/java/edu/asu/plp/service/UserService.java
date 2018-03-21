package edu.asu.plp.service;

import edu.asu.plp.user.dao.UserDAO;
import edu.asu.plp.user.model.User;
import edu.asu.plp.user.model.UserCred;
import edu.asu.plp.user.model.UserInfo;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    ApplicationContext context =
            new ClassPathXmlApplicationContext("spring-module.xml");

    UserDAO userDAO = (UserDAO) context.getBean("userDAO");

    /***
     * @brief This method save the user information in the database
     * @param user
     * @return string as success or error
     */
    public String saveUser(User user) {
        return userDAO.saveUser(user);
    }

    /***
     * @brief This method check if the database is configured properly or not
     * @return true if the database is configured properly otherwise false
     */
    public boolean isDatabaseConnected() {
        return userDAO.isDatabaseConnected();
    }

    public String registerUser(UserInfo uInfo, UserCred uCred){
        return userDAO.registerUser(uInfo, uCred);
    }

    public String updateUserInformation(UserInfo uInfoOld, UserInfo uInfoNew){
        return userDAO.updateUser(uInfoOld, uInfoNew);
    }

    public String getUserInfo(UserInfo uInfo){
        return userDAO.getUserInfo(uInfo);
    }

    public String changePassword(UserCred uCredOld, UserCred uCredNew){
        return userDAO.changePassword(uCredOld, uCredNew);
    }

    public boolean authenticateUser(UserCred uCred){
        return userDAO.authenticateUser(uCred);
    }
}
