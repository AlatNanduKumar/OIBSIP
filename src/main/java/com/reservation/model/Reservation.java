package com.reservation.model;

import java.time.LocalDate;

/** One booked ticket. */
public record Reservation(String pnr, String passengerName, int trainNo, String trainName,
                          String classType, LocalDate journeyDate, String source, String destination) {

    public String toDetailsText() {
        return String.format(
                "%-18s: %s%n%-18s: %s%n%-18s: %d - %s%n%-18s: %s%n%-18s: %s%n%-18s: %s%n%-18s: %s",
                "PNR Number", pnr,
                "Passenger Name", passengerName,
                "Train", trainNo, trainName,
                "Class", classType,
                "Date of Journey", journeyDate,
                "From", source,
                "To", destination);
    }
}
