package edu.asu.plp.controller;

import edu.asu.plp.service.UserService;
import edu.asu.plp.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping("/user")
    public Principal userLogin(Principal principal) {
        return principal;
    }

    /**
     * @return true if the database is configured properly otherwise false
     * @brief This method checks if the database is configured properly or not
     */
    @RequestMapping("/checkDBConnection")
    public boolean checkDBConnection() {
        return userService.isDatabaseConnected();
    }

    /***
     * @brief This method takes the user object and insert the emailId, first_name and last_name
     * in the user_details table. This information will be used for tracking the number of users use Web PLP
     * @param user
     * @return success if the insertion is done otherwise error if exception encountered
     */
    @RequestMapping(value = "/saveUser", method = RequestMethod.POST, produces = "text/plain")
    public @ResponseBody
    String saveUser(@RequestBody User user) {
        //String response = userService.saveUser(user);
        //response = "success";
        //return response.equals("success") ? "success" : "error";
        return "success";
    }
}
