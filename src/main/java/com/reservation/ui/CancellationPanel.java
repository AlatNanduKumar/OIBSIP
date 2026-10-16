package com.reservation.ui;

import com.reservation.dao.ReservationDAO;
import com.reservation.model.Reservation;
import com.reservation.util.Validator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.SQLException;

/** Cancellation form: fetch a booking by PNR, review it, confirm, and delete it. */
public class CancellationPanel extends JPanel {
    private final JTextField pnrField = new JTextField(14);
    private final JTextArea detailsArea = new JTextArea(9, 34);
    private final JButton cancelButton = new JButton("Cancel This Ticket");
    private Reservation fetched; // the booking currently shown, or null

    public CancellationPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JButton fetch = new JButton("Fetch");
        fetch.addActionListener(e -> fetchBooking());
        pnrField.addActionListener(e -> fetchBooking()); // Enter key also fetches

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        top.add(new JLabel("PNR Number:"));
        top.add(pnrField);
        top.add(fetch);

        detailsArea.setEditable(false);
        detailsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        detailsArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        detailsArea.setText("Enter a PNR and click Fetch to see the booking details.");
        JScrollPane scroll = new JScrollPane(detailsArea);
        scroll.setBorder(BorderFactory.createTitledBorder("Booking Details"));

        cancelButton.setEnabled(false);
        cancelButton.addActionListener(e -> confirmAndCancel());
        JPanel bottom = new JPanel();
        bottom.add(cancelButton);

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        // Changing the PNR text invalidates whatever booking is on screen.
        pnrField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { reset(); }
            public void removeUpdate(DocumentEvent e) { reset(); }
            public void changedUpdate(DocumentEvent e) { reset(); }
        });
    }

    private void reset() {
        fetched = null;
        cancelButton.setEnabled(false);
    }

    private void fetchBooking() {
        String error = Validator.validatePnr(pnrField.getText());
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Invalid PNR", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Reservation r = ReservationDAO.findByPnr(pnrField.getText().trim());
            if (r == null) {
                detailsArea.setText("No booking found for this PNR.");
                reset();
                JOptionPane.showMessageDialog(this, "No booking found for PNR " + pnrField.getText().trim() + ".",
                        "Not Found", JOptionPane.INFORMATION_MESSAGE);
            } else {
                fetched = r;
                detailsArea.setText(r.toDetailsText());
                cancelButton.setEnabled(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void confirmAndCancel() {
        if (fetched == null) return;
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel the ticket with PNR " + fetched.pnr() + "?",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            boolean removed = ReservationDAO.cancel(fetched.pnr());
            JOptionPane.showMessageDialog(this,
                    removed ? "Ticket cancelled successfully. PNR " + fetched.pnr() + " has been removed."
                            : "This ticket was already cancelled.",
                    "Cancellation", JOptionPane.INFORMATION_MESSAGE);
            pnrField.setText(""); // also triggers reset()
            detailsArea.setText("Enter a PNR and click Fetch to see the booking details.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not cancel: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
