package edu.asu.plp.user.dao;

import edu.asu.plp.user.model.User;

/***
 * @brief This interface is used to handle user information
 */

public interface UserDAO {
    String saveUser(User user);

    boolean isDatabaseConnected();
}

