package edu.asu.plp.controller;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.asu.plp.service.PLPUserDB;
import edu.asu.plp.service.UserService;
import edu.asu.plp.user.model.User;
import edu.asu.plp.user.model.UserCred;
import edu.asu.plp.user.model.UserInfo;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;

@RestController
public class UserController {

    @Autowired
    UserService userService;
    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(UserController.class);
    public static final Map<Object, String> userEmailMap = new HashMap<>();
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
        String response = userService.saveUser(user);
        return response.equals("success") ? "success" : "error";
    }

    /*
     * @brief This method takes the user information and user credentials from request body json and
     * creates a user information object and user credential object.
     * This information is inserted in the database
     * once the entry is done successfully in the database, session id is created and maintained
     * @param request body json, request object, response object
     * @return success json if the insertion is done otherwise failure or error message if exception encountered
     */
    @RequestMapping(value = "/registerUser", method = RequestMethod.POST, produces = "text/plain")
    public @ResponseBody String registerUser(@RequestBody String json, HttpServletRequest request, HttpServletResponse response) {
        String responseDB = "failure";
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = new HashMap<String, Object>();
            // convert JSON string to Map
            map = mapper.readValue(json, new TypeReference<Map<String, String>>(){});

            Object o = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            userEmailMap.put(o,(String)map.getOrDefault("email", "guest"));
            ThreadContext.put("username",userEmailMap.get(o));

            UserInfo uInfo = new UserInfo();
            uInfo.setName((String)map.getOrDefault("name", "guest"));
            uInfo.setEmail((String)map.getOrDefault("email", "guest"));
            uInfo.setOrg_school((String)map.getOrDefault("org_school", "guest"));
            uInfo.setGender((String)map.getOrDefault("gender", "guest"));
            uInfo.setDateOfBirth((String)map.getOrDefault("dateOfBirth", "guest"));
            uInfo.setContact_no((String)map.getOrDefault("contact_no", "guest"));
            uInfo.setAlt_no((String)map.getOrDefault("alt_no", "guest"));
            uInfo.setProfile_photo((String)map.getOrDefault("profile_photo", "guest"));

            UserCred uCred = new UserCred();
            uCred.setUsername((String)map.getOrDefault("email", "guest"));
            uCred.setPassword((String)map.getOrDefault("password", "guest"));

            responseDB = userService.registerUser(uInfo, uCred);

            if(responseDB.equalsIgnoreCase("success")){
                HttpSession session = request.getSession();
                PLPUserDB.getInstance().registerUserSession(uInfo.getEmail(), session, session.getId());
            }
            logger.info(map.getOrDefault("name", "guest") + "User registered successfully");
        } catch (JsonGenerationException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } catch (JsonMappingException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }finally{
            return "{\"status\" : \"" + responseDB + "\"}";
        }
    }

    /*
     * @brief This method takes the new and old user information from request body json and
     * creates two user information objects.
     * This information is updated in the database
     * once the entry is updated successfully in the database, session id is updated and maintained
     * @param request body json, request object, response object
     * @return success json if the update is done otherwise failure or error message if exception encountered
     */
    @RequestMapping(value = "/updateUser", method = RequestMethod.POST, produces = "text/plain")
    public @ResponseBody String updateUser(@RequestBody String json, HttpServletRequest request, HttpServletResponse response) {
        String responseDB = "Unable to update User";
        try {

            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> map = new HashMap<String, Object>();

            // convert JSON string to Map
            map = mapper.readValue(json, new TypeReference<Map<String, String>>(){});

            UserInfo uInfoNew = new UserInfo();
            uInfoNew.setName((String)map.getOrDefault("name", "guest"));
            uInfoNew.setEmail((String)map.getOrDefault("newEmail", "guest"));
            uInfoNew.setOrg_school((String)map.getOrDefault("org_school", "guest"));
            uInfoNew.setGender((String)map.getOrDefault("gender", "guest"));
            uInfoNew.setDateOfBirth((String)map.getOrDefault("dateOfBirth", "guest"));
            uInfoNew.setContact_no((String)map.getOrDefault("contact_no", "guest"));
            uInfoNew.setAlt_no((String)map.getOrDefault("alt_no", "guest"));
            uInfoNew.setProfile_photo((String)map.getOrDefault("profile_photo", "guest"));


            UserInfo uInfoOld = new UserInfo();
            uInfoOld.setEmail((String)map.getOrDefault("oldEmail", "guest"));


            HttpSession session = request.getSession(false);

            if(session != null && PLPUserDB.getInstance().userSessionPresent(session.getId())){
                responseDB = userService.updateUserInformation(uInfoOld, uInfoNew);

                if(responseDB.equalsIgnoreCase("success")){
                    PLPUserDB.getInstance().registerUserSession(uInfoNew.getEmail(), session, session.getId());
                }
            }


        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            return "{\"status\" : \"" + responseDB + "\"}";
        }
    }

    /*
     * @brief This is the get method that returns the user information of the user whose email id is
     * passed in the query parameter
     * @param query parameter user email, request object, response object
     * @return user information json if the email is valid otherwise failure or error message if exception encountered
     */
    @RequestMapping(value = "/getUserInfo", method = RequestMethod.GET, produces = "text/plain")
    public @ResponseBody String getUserInfo(@RequestParam("email") String email, HttpServletRequest request, HttpServletResponse response) {
        String responseDB = "User not logged in";
        ObjectMapper mapper = new ObjectMapper();
        try {

            Object o = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            userEmailMap.put(o,email);
            ThreadContext.put("username",userEmailMap.get(o));

            UserInfo uInfo = new UserInfo();
            uInfo.setEmail(email);

            HttpSession session = request.getSession(false);
            if(session != null && PLPUserDB.getInstance().userSessionPresent(session.getId())) {
                responseDB = userService.getUserInfo(uInfo);
            }
            responseDB = responseDB.equalsIgnoreCase("success") ? mapper.writeValueAsString(uInfo) : "{\"status\" : \"" + responseDB + "\"}";
            logger.info("User logged in successfully");
        } catch (JsonGenerationException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } catch (JsonMappingException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }finally{
            return responseDB ;
        }
    }

    /*
     * @brief This is method updates the users password by checking if he is a valid user through his old password first
     * @param request body json, request object, response object
     * @return success json if the update is done otherwise failure or error message if exception encountered
     */
    @RequestMapping(value = "/changePassword", method = RequestMethod.POST, produces = "text/plain")
    public @ResponseBody String changePassword(@RequestBody String json, HttpServletRequest request, HttpServletResponse response) {
        String responseDB = "Invalid Username or Password";
        try {

            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> map = new HashMap<String, Object>();

            // convert JSON string to Map
            map = mapper.readValue(json, new TypeReference<Map<String, String>>(){});

            UserCred uCredOld = new UserCred();
            uCredOld.setUsername((String)map.getOrDefault("email", ""));
            uCredOld.setPassword((String)map.getOrDefault("old_password", ""));

            UserCred uCredNew = new UserCred();
            uCredNew.setUsername((String)map.getOrDefault("email", ""));
            uCredNew.setPassword((String)map.getOrDefault("new_password", ""));

            HttpSession session = request.getSession(false);

            if(session != null && PLPUserDB.getInstance().userSessionPresent(session.getId())) {
                responseDB = userService.changePassword(uCredOld, uCredNew);
            }
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            return "{\"status\" : \"" + responseDB + "\"}";
        }
    }

    /*
     * @brief This method authenticates the user by parsing the request body json and using the database to authenticate
     * @param request body json, request object, response object
     * @return success json if the authentication is done otherwise failure or error message if exception encountered
     */
    @RequestMapping(value = "/authenticateUser", method = RequestMethod.POST, produces = "text/plain")
    public @ResponseBody String authenticateUser(@RequestBody String json, HttpServletRequest request, HttpServletResponse response) {
        boolean responseDB = false;
        try {

            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> map = new HashMap<String, Object>();

            // convert JSON string to Map
            map = mapper.readValue(json, new TypeReference<Map<String, String>>(){});

            UserCred uCred = new UserCred();
            uCred.setUsername((String)map.getOrDefault("email", "guest"));
            uCred.setPassword((String)map.getOrDefault("password", "guest"));

            responseDB = userService.authenticateUser(uCred);
            if(responseDB){
                HttpSession session = request.getSession();
                PLPUserDB.getInstance().registerUserSession(uCred.getUsername(), session, session.getId());
            }
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            return "{\"status\" : \"" + responseDB + "\"}";
        }
    }

    /*
     * @brief This is logs out the user by invalidating their session and
     * @param request body json, request object, response object
     * @return success json if the update is done otherwise failure or error message if exception encountered
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET, produces = "text/plain")
    public @ResponseBody String logout(HttpServletRequest request, HttpServletResponse response) {
        String responseDB = "failure";
        Object o = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ThreadContext.put("username",userEmailMap.get(o));

        try {
            HttpSession session = request.getSession(false);

            if(session != null && PLPUserDB.getInstance().userSessionPresent(session.getId())) {
                session.invalidate();
                PLPUserDB.getInstance().removeUserSession(session.getId());
                responseDB = "success";
                logger.info("User logged out successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }finally{
            return "{\"status\" : \"" + responseDB + "\"}";
        }
    }
}
