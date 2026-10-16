package com.reservation;

import com.reservation.db.DatabaseHelper;
import com.reservation.ui.LoginFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.sql.SQLException;

/** Application entry point: prepares the database, then shows the login screen. */
public class Main {
    public static void main(String[] args) {
        try {
            DatabaseHelper.init();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Could not initialise the database:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        SwingUtilities.invokeLater(() -> {
            useNimbus();
            new LoginFrame().setVisible(true);
        });
    }

    public static void useNimbus() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
        } catch (Exception ignored) {
            // fall back to the default look and feel
        }
    }
}
