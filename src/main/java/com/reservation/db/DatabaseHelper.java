package com.reservation.db;

import com.reservation.util.PasswordUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** Creates the SQLite connection, the tables, and the starter data. */
public final class DatabaseHelper {
    private static String url = "jdbc:sqlite:reservation.db";

    private DatabaseHelper() { }

    /** Lets tests or other tools point the app at a different database file. */
    public static void setUrl(String jdbcUrl) {
        url = jdbcUrl;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    public static void init() throws SQLException {
        try (Connection c = getConnection(); Statement st = c.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS users ("
                    + "username TEXT PRIMARY KEY, salt TEXT NOT NULL, password_hash TEXT NOT NULL)");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS trains ("
                    + "train_no INTEGER PRIMARY KEY, train_name TEXT NOT NULL)");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS reservations ("
                    + "pnr TEXT PRIMARY KEY, passenger_name TEXT NOT NULL, train_no INTEGER NOT NULL, "
                    + "train_name TEXT NOT NULL, class_type TEXT NOT NULL, journey_date TEXT NOT NULL, "
                    + "source TEXT NOT NULL, destination TEXT NOT NULL)");
            seedUsers(c);
            seedTrains(c);
        }
    }

    private static void seedUsers(Connection c) throws SQLException {
        if (isEmpty(c, "users")) {
            String salt = PasswordUtil.newSalt();
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO users(username, salt, password_hash) VALUES (?,?,?)")) {
                ps.setString(1, "admin");
                ps.setString(2, salt);
                ps.setString(3, PasswordUtil.hash(salt, "admin123"));
                ps.executeUpdate();
            }
        }
    }

    private static void seedTrains(Connection c) throws SQLException {
        if (!isEmpty(c, "trains")) return;
        Object[][] trains = {
                {12723, "Telangana Express"},
                {12701, "Hussainsagar Express"},
                {12759, "Charminar Express"},
                {12797, "Venkatadri Express"},
                {12616, "Grand Trunk Express"},
                {12627, "Karnataka Express"},
                {12951, "Mumbai Rajdhani Express"},
                {12301, "Howrah Rajdhani Express"},
                {12009, "Shatabdi Express"},
                {12839, "Howrah Mail"},
        };
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO trains(train_no, train_name) VALUES (?,?)")) {
            for (Object[] t : trains) {
                ps.setInt(1, (Integer) t[0]);
                ps.setString(2, (String) t[1]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static boolean isEmpty(Connection c, String table) throws SQLException {
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + table)) {
            return rs.next() && rs.getInt(1) == 0;
        }
    }
}
