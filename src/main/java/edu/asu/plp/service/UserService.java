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

    /***
     * @brief This method calls the registerUser method of the DAO which saves the user information and credential in the database
     * @param uInfo user information object
     * @param uCred user credentials object
     * @return string as success or error message
     */
    public String registerUser(UserInfo uInfo, UserCred uCred){
        return userDAO.registerUser(uInfo, uCred);
    }

    /***
     * @brief This method calls the updateUser method of the DAO which updates the user information in the database
     * @param uInfoOld old user information object
     * @param uInfoNew new user information object
     * @return string as success or error message
     */
    public String updateUserInformation(UserInfo uInfoOld, UserInfo uInfoNew){
        return userDAO.updateUser(uInfoOld, uInfoNew);
    }

    /***
     * @brief This method calls the getUserInfo method of the DAO which retrieves the user information from the database
     * @param uInfo user information (email) object of the user to be retrieved
     * @return string as success or error message
     */
    public String getUserInfo(UserInfo uInfo){
        String status = userDAO.getUserInfo(uInfo);
        uInfo.checkProfileComplete();
        return status;
    }

    /***
     * @brief This method calls the changePassword method of the DAO which updates the user credentials in the database
     * @param uCredOld old user credentials object
     * @param uCredNew new user credentials object
     * @return string as success or error message
     */
    public String changePassword(UserCred uCredOld, UserCred uCredNew){
        return userDAO.changePassword(uCredOld, uCredNew);
    }

    /***
     * @brief This method calls the authenticateUser method of the DAO which checks the given password is authentic
     * from the database
     * @param uCred user credentials object
     * @return boolean as true or false
     */
    public boolean authenticateUser(UserCred uCred){
        return userDAO.authenticateUser(uCred);
    }
}
