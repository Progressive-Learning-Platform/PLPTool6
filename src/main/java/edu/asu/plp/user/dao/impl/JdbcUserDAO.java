package edu.asu.plp.user.dao.impl;

import edu.asu.plp.tool.backend.util.PasswordUtil;
import edu.asu.plp.user.dao.UserDAO;
import edu.asu.plp.user.model.User;
import edu.asu.plp.user.model.UserCred;
import edu.asu.plp.user.model.UserInfo;

import javax.sql.DataSource;
import java.sql.*;

/***
 * This class is to perform database operations on the User object
 */
public class JdbcUserDAO implements UserDAO {
    private DataSource dataSource;

    /***
     * @brief This function sets the data source
     * @param dataSource
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /***
     * @brief This function check if the user is already exist in the database. If the user is not present then
     * the user details will be inserted in the database
     * @param user
     * @return success if no exception occurs otherwise exception will be returned
     */
    public String saveUser(User user) {
        String sql = "INSERT INTO user_info " +
                "(email_id, first_name, last_name) VALUES (?, ?, ?)";

        String preCheck = "SELECT count(*) as rowcount from user_info WHERE email_id = ?";

        Connection conn = null;

        try {
            conn = dataSource.getConnection();
            PreparedStatement preCheckPS = conn.prepareStatement(preCheck);
            preCheckPS.setString(1, user.getEmail());
            ResultSet rs = preCheckPS.executeQuery();
            rs.next();
            int count = rs.getInt("rowcount");
            if (count == 1)
                return "success";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getFirstName());
            ps.setString(3, user.getLastName());
            ps.executeUpdate();
            ps.close();

        } catch (SQLException e) {
            return e.getMessage();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    return e.getMessage();
                }
            }
        }
        return "success";
    }

    @Override
    public String registerUser(UserInfo uInfo, UserCred uCred) {
        String result = "failure";
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            CallableStatement cStmt = conn.prepareCall("{call insert_user(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");

            String salt = PasswordUtil.getRandomSalt();
            String hashedPwd = PasswordUtil.hash(uCred.getPassword(), salt);

            cStmt.setString(1, uInfo.getName());
            cStmt.setString(2, uInfo.getEmail());
            cStmt.setString(3, uInfo.getOrg_school());
            cStmt.setString(4, uInfo.getGender());
            cStmt.setString(5, uInfo.getDateOfBirth());
            cStmt.setString(6, uInfo.getContact_no());
            cStmt.setString(7, uInfo.getAlt_no());
            cStmt.setString(8, uInfo.getProfile_photo());
            cStmt.setString(9, salt);
            cStmt.setString(10, hashedPwd);
            cStmt.registerOutParameter(11, Types.VARCHAR);

            cStmt.execute();

            result = cStmt.getString(11);
        } catch (SQLException e) {
            return e.getMessage();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    return e.getMessage();
                }
            }
        }
        return result;
    }

    @Override
    public String updateUser(UserInfo uInfoOld, UserInfo uInforNew) {
        String result = "failure";
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            CallableStatement cStmt = conn.prepareCall("{call edit_user(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");

            cStmt.setString(1, uInfoOld.getEmail());
            cStmt.setString(2, uInforNew.getName());
            cStmt.setString(3, uInforNew.getEmail());
            cStmt.setString(4, uInforNew.getOrg_school());
            cStmt.setString(5, uInforNew.getGender());
            cStmt.setString(6, uInforNew.getDateOfBirth());
            cStmt.setString(7, uInforNew.getContact_no());
            cStmt.setString(8, uInforNew.getAlt_no());
            cStmt.setString(9, uInforNew.getProfile_photo());
            cStmt.registerOutParameter(10, Types.VARCHAR);

            cStmt.execute();

            result = cStmt.getString(10);
        } catch (SQLException e) {
            return e.getMessage();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    return e.getMessage();
                }
            }
        }
        return result;

    }

    @Override
    public String getUserInfo(UserInfo uInfo) {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            CallableStatement cStmt = conn.prepareCall("{call get_user_info(?)}");

            cStmt.setString(1, uInfo.getEmail());

            rs = cStmt.executeQuery();

            if(rs.next()) {
                uInfo.setName(rs.getString("name"));
                uInfo.setEmail(rs.getString("email_id"));
                uInfo.setOrg_school(rs.getString("org_school"));
                uInfo.setGender(rs.getString("gender"));
                uInfo.setDateOfBirth(rs.getString("dateofbirth"));
                uInfo.setContact_no(rs.getString("contact_no"));
                uInfo.setAlt_no(rs.getString("alt_no"));
                uInfo.setProfile_photo(rs.getString("profile_photo"));
            }else{
                return "failure";
            }
        } catch (SQLException e) {
            return e.getMessage();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    return e.getMessage();
                }
            }
        }
        return "success";
    }

    @Override
    public String changePassword(UserCred uCredOld, UserCred uCredNew) {
        String result = "failure";
        Connection conn = null;
        if(authenticateUser(uCredOld)){
            String passwordSalt = getUserSalt(uCredOld);
            String hashedPwd = PasswordUtil.hash(uCredNew.getPassword(), passwordSalt);
            try {
                conn = dataSource.getConnection();
                CallableStatement cStmt = conn.prepareCall("{call change_password(?, ?, ?, ?)}");

                cStmt.setString(1, uCredOld.getUsername());
                cStmt.setString(2, passwordSalt);
                cStmt.setString(3, hashedPwd);
                cStmt.registerOutParameter(4, Types.VARCHAR);

                cStmt.execute();

                result = cStmt.getString(4);
            } catch (SQLException e) {
                return e.getMessage();
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        return e.getMessage();
                    }
                }
            }
        }

        return result;
    }

    @Override
    public boolean authenticateUser(UserCred uCred) {
        ResultSet rs = null;
        Connection conn = null;
        String passwordSalt = getUserSalt(uCred);
        String hashedPwd = PasswordUtil.hash(uCred.getPassword(), passwordSalt);
        Integer res = 0;
        try {
            conn = dataSource.getConnection();
            CallableStatement cStmt = conn.prepareCall("{call authenticate_user(?, ?)}");


            cStmt.setString(1, uCred.getUsername());
            cStmt.setString(2, hashedPwd);


            rs = cStmt.executeQuery();
            if(rs.next()) {
                res = rs.getInt("auth");
            }
        } catch (SQLException e) {
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    return false;
                }
            }
        }
        return (res == 1);
    }

    private String getUserSalt(UserCred uCred){
        ResultSet rs = null;
        Connection conn = null;
        String passwordSalt = "";
        try {
            conn = dataSource.getConnection();
            CallableStatement cStmt = conn.prepareCall("{call get_user_salt(?)}");


            cStmt.setString(1, uCred.getUsername());


            rs = cStmt.executeQuery();
            if(rs.next()) {
                passwordSalt = rs.getString("password_salt");
            }
        } catch (SQLException e) {
            return e.getMessage();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    return e.getMessage();
                }
            }
        }
        return passwordSalt;
    }

    /***
     * @brief This function checks if the database is connected or not
     * @return true of the database is connected otherwise false
     */
    @Override
    public boolean isDatabaseConnected() {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            if (conn != null) return true;

        } catch (SQLException e) {
            return false;
        }
        return true;
    }
}