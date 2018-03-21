package edu.asu.plp.user.dao;

import edu.asu.plp.user.model.User;
import edu.asu.plp.user.model.UserCred;
import edu.asu.plp.user.model.UserInfo;

/***
 * @brief This interface is used to handle user information
 */

public interface UserDAO {
    String saveUser(User user);

    String registerUser(UserInfo uInfo, UserCred uCred);

    String updateUser(UserInfo uInfoOld, UserInfo uInfoNew);

    String getUserInfo(UserInfo uInfo);

    String changePassword(UserCred uCredOld, UserCred uCredNew);

    boolean authenticateUser(UserCred uCred);

    boolean isDatabaseConnected();
}

