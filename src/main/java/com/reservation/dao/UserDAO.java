package com.reservation.dao;

import com.reservation.db.DatabaseHelper;
import com.reservation.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class UserDAO {
    private UserDAO() { }

    /** True only when the username exists and the password matches the stored salted hash. */
    public static boolean authenticate(String username, String password) throws SQLException {
        String sql = "SELECT salt, password_hash FROM users WHERE username = ?";
        try (Connection c = DatabaseHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        && PasswordUtil.matches(rs.getString("salt"), password, rs.getString("password_hash"));
            }
        }
    }
}
