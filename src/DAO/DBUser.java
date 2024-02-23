package DAO;

import model.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUser {
    public static User getUser(String username) throws SQLException {
        User user = null;

        // Search the database for the user with the username
        String sql = "SELECT * FROM users WHERE user_name =?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            // Get user's id and password
            int userId = rs.getInt("user_id");
            String password = rs.getString("password");
            user = new User(userId, username, password);
        }

        return user;
    }
}
