package edu.asu.plp.user.dao.impl;

import edu.asu.plp.user.dao.UserDAO;
import edu.asu.plp.user.model.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcUserDAO implements UserDAO {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String saveUser(User user) {
        String sql = "INSERT INTO user_info " +
                "(email_id, first_name, last_name) VALUES (?, ?, ?)";

        String preCheck = "SELECT count(*) as rowcount from user_info WHERE email_id = ?";

        Connection conn = null;

        try {
            conn = dataSource.getConnection();
            PreparedStatement preCheckPS = conn.prepareStatement(preCheck);
            preCheckPS.setString(1,user.getEmail());
            ResultSet rs = preCheckPS.executeQuery();
            rs.next();
            int count = rs.getInt("rowcount");
            if(count == 1)
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
    public boolean isDatabaseConnected() {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            if(conn != null) return true;

        } catch (SQLException e) {
            return false;
        }
        return true;
    }
}