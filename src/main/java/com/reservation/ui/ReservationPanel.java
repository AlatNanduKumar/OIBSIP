package com.reservation.ui;

import com.reservation.dao.ReservationDAO;
import com.reservation.dao.TrainDAO;
import com.reservation.model.Reservation;
import com.reservation.util.Validator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Reservation form. The train name fills itself in as the train number is typed. */
public class ReservationPanel extends JPanel {
    private final JTextField nameField = new JTextField(22);
    private final JTextField trainNoField = new JTextField(22);
    private final JTextField trainNameField = new JTextField(22);
    private final JComboBox<String> classBox = new JComboBox<>(
            new String[]{"Sleeper", "AC 3 Tier", "AC 2 Tier", "AC First Class", "Chair Car"});
    private final JTextField dateField = new JTextField(22);
    private final JTextField sourceField = new JTextField(22);
    private final JTextField destField = new JTextField(22);

    public ReservationPanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        trainNameField.setEditable(false);
        trainNameField.setToolTipText("Filled in automatically from the train number");
        dateField.setToolTipText("Format: " + Validator.DATE_PATTERN);

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;

        int row = 0;
        row = addRow(g, row, "Passenger Name *", nameField);
        row = addRow(g, row, "Train Number *", trainNoField);
        row = addRow(g, row, "Train Name (auto)", trainNameField);
        row = addRow(g, row, "Class Type *", classBox);
        row = addRow(g, row, "Date of Journey *", dateField);

        JLabel hint = new JLabel("Format: " + Validator.DATE_PATTERN + "   (e.g. 2026-12-25)");
        hint.setForeground(Color.GRAY);
        hint.setFont(hint.getFont().deriveFont(Font.ITALIC, 11f));
        g.gridx = 1; g.gridy = row++;
        add(hint, g);

        row = addRow(g, row, "Source Station *", sourceField);
        row = addRow(g, row, "Destination Station *", destField);

        JButton book = new JButton("Book Ticket");
        JButton clear = new JButton("Clear");
        book.addActionListener(e -> book());
        clear.addActionListener(e -> clearForm());
        JPanel buttons = new JPanel();
        buttons.add(book);
        buttons.add(clear);
        g.gridx = 0; g.gridy = row; g.gridwidth = 2;
        g.insets = new Insets(18, 6, 6, 6);
        add(buttons, g);

        trainNoField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateTrainName(); }
            public void removeUpdate(DocumentEvent e) { updateTrainName(); }
            public void changedUpdate(DocumentEvent e) { updateTrainName(); }
        });
    }

    private int addRow(GridBagConstraints g, int row, String label, java.awt.Component field) {
        g.gridwidth = 1;
        g.gridx = 0; g.gridy = row;
        add(new JLabel(label), g);
        g.gridx = 1;
        add(field, g);
        return row + 1;
    }

    /** Auto-populates the train name from the train number. */
    private void updateTrainName() {
        String text = trainNoField.getText().trim();
        if (text.isEmpty()) {
            trainNameField.setText("");
        } else if (!text.matches("\\d{1,6}")) {
            trainNameField.setText("Train number must be numeric");
        } else {
            try {
                Optional<String> name = TrainDAO.findName(Integer.parseInt(text));
                trainNameField.setText(name.orElse("Train not found"));
            } catch (SQLException ex) {
                trainNameField.setText("Database error");
            }
        }
    }

    private void book() {
        List<String> errors = Validator.validateBooking(nameField.getText(), trainNoField.getText(),
                dateField.getText(), sourceField.getText(), destField.getText());
        if (!errors.isEmpty()) {
            showErrors(errors);
            return;
        }
        try {
            int trainNo = Integer.parseInt(trainNoField.getText().trim());
            Optional<String> trainName = TrainDAO.findName(trainNo);
            if (trainName.isEmpty()) {
                showErrors(List.of("Train number " + trainNo + " does not exist."));
                return;
            }
            Reservation draft = new Reservation(null, nameField.getText().trim(), trainNo, trainName.get(),
                    (String) classBox.getSelectedItem(), Validator.parseDate(dateField.getText()),
                    sourceField.getText().trim(), destField.getText().trim());
            Reservation saved = ReservationDAO.book(draft);

            JTextArea details = new JTextArea(
                    "Your ticket has been booked successfully!\n\n" + saved.toDetailsText());
            details.setEditable(false);
            details.setOpaque(false);
            details.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
            JOptionPane.showMessageDialog(this, details, "Booking Confirmed",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not save the booking: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showErrors(List<String> errors) {
        StringBuilder sb = new StringBuilder("Please fix the following:\n\n");
        for (String err : errors) sb.append("  \u2022 ").append(err).append('\n');
        JOptionPane.showMessageDialog(this, sb.toString(), "Invalid Input", JOptionPane.WARNING_MESSAGE);
    }

    private void clearForm() {
        nameField.setText("");
        trainNoField.setText("");
        trainNameField.setText("");
        classBox.setSelectedIndex(0);
        dateField.setText("");
        sourceField.setText("");
        destField.setText("");
        nameField.requestFocusInWindow();
    }
}
