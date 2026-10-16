package com.reservation.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

/** Input validation for the booking form. Each method returns a list of readable error messages. */
public final class Validator {
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

    private Validator() { }

    public static List<String> validateBooking(String name, String trainNo, String date,
                                               String source, String destination) {
        List<String> errors = new ArrayList<>();

        if (isBlank(name)) {
            errors.add("Passenger name is required.");
        } else if (!name.trim().matches("[A-Za-z][A-Za-z .'-]*")) {
            errors.add("Passenger name may contain only letters, spaces, '.', '-' and apostrophes.");
        }

        if (isBlank(trainNo)) {
            errors.add("Train number is required.");
        } else if (!trainNo.trim().matches("\\d{1,6}")) {
            errors.add("Train number must be numeric (digits only).");
        }

        if (isBlank(date)) {
            errors.add("Date of journey is required.");
        } else {
            LocalDate parsed = parseDate(date);
            if (parsed == null) {
                errors.add("Date of journey must be a valid date in " + DATE_PATTERN + " format.");
            } else if (parsed.isBefore(LocalDate.now())) {
                errors.add("Date of journey cannot be in the past.");
            }
        }

        if (isBlank(source)) errors.add("Source station is required.");
        if (isBlank(destination)) errors.add("Destination station is required.");
        if (!isBlank(source) && !isBlank(destination)
                && source.trim().equalsIgnoreCase(destination.trim())) {
            errors.add("Source and destination stations must be different.");
        }
        return errors;
    }

    /** Returns the parsed date, or null when the text is not a valid yyyy-MM-dd date. */
    public static LocalDate parseDate(String text) {
        try {
            return LocalDate.parse(text.trim(), DATE_FMT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /** A PNR is exactly 10 digits. */
    public static String validatePnr(String pnr) {
        if (isBlank(pnr)) return "PNR number is required.";
        if (!pnr.trim().matches("\\d{10}")) return "PNR must be a 10-digit number.";
        return null;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
