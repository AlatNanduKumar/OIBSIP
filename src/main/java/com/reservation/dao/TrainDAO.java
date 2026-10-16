package com.reservation.dao;

import com.reservation.db.DatabaseHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public final class TrainDAO {
    private TrainDAO() { }

    public static Optional<String> findName(int trainNo) throws SQLException {
        String sql = "SELECT train_name FROM trains WHERE train_no = ?";
        try (Connection c = DatabaseHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, trainNo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(rs.getString(1)) : Optional.empty();
            }
        }
    }
}
