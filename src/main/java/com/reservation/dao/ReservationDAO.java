package com.reservation.dao;

import com.reservation.db.DatabaseHelper;
import com.reservation.model.Reservation;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public final class ReservationDAO {
    private static final SecureRandom RANDOM = new SecureRandom();

    private ReservationDAO() { }

    /** Saves the booking with a freshly generated unique 10-digit PNR and returns the saved record. */
    public static Reservation book(Reservation r) throws SQLException {
        try (Connection c = DatabaseHelper.getConnection()) {
            String pnr;
            do {
                pnr = String.valueOf(1_000_000_000L + (long) (RANDOM.nextDouble() * 9_000_000_000L));
            } while (pnrExists(c, pnr));

            String sql = "INSERT INTO reservations(pnr, passenger_name, train_no, train_name, "
                    + "class_type, journey_date, source, destination) VALUES (?,?,?,?,?,?,?,?)";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, pnr);
                ps.setString(2, r.passengerName());
                ps.setInt(3, r.trainNo());
                ps.setString(4, r.trainName());
                ps.setString(5, r.classType());
                ps.setString(6, r.journeyDate().toString());
                ps.setString(7, r.source());
                ps.setString(8, r.destination());
                ps.executeUpdate();
            }
            return new Reservation(pnr, r.passengerName(), r.trainNo(), r.trainName(),
                    r.classType(), r.journeyDate(), r.source(), r.destination());
        }
    }

    /** Returns the booking for this PNR, or null when none exists. */
    public static Reservation findByPnr(String pnr) throws SQLException {
        String sql = "SELECT * FROM reservations WHERE pnr = ?";
        try (Connection c = DatabaseHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, pnr);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new Reservation(rs.getString("pnr"), rs.getString("passenger_name"),
                        rs.getInt("train_no"), rs.getString("train_name"), rs.getString("class_type"),
                        LocalDate.parse(rs.getString("journey_date")),
                        rs.getString("source"), rs.getString("destination"));
            }
        }
    }

    /** Deletes the booking. Returns false if the PNR did not exist (e.g. already cancelled). */
    public static boolean cancel(String pnr) throws SQLException {
        String sql = "DELETE FROM reservations WHERE pnr = ?";
        try (Connection c = DatabaseHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, pnr);
            return ps.executeUpdate() > 0;
        }
    }

    private static boolean pnrExists(Connection c, String pnr) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT 1 FROM reservations WHERE pnr = ?")) {
            ps.setString(1, pnr);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
