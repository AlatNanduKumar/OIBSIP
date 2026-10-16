package com.reservation.ui;

import com.reservation.dao.UserDAO;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;

/** Login screen: username + password. Invalid credentials show "Access Denied". */
public class LoginFrame extends JFrame {
    private final JTextField userField = new JTextField(18);
    private final JPasswordField passField = new JPasswordField(18);

    public LoginFrame() {
        super("Online Reservation System - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Online Reservation System", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(title, g);

        JLabel sub = new JLabel("Please log in to continue", SwingConstants.CENTER);
        g.gridy = 1;
        p.add(sub, g);

        g.gridwidth = 1; g.gridy = 2; g.gridx = 0;
        p.add(new JLabel("Username:"), g);
        g.gridx = 1;
        p.add(userField, g);

        g.gridy = 3; g.gridx = 0;
        p.add(new JLabel("Password:"), g);
        g.gridx = 1;
        p.add(passField, g);

        JButton login = new JButton("Login");
        login.addActionListener(e -> doLogin());
        g.gridy = 4; g.gridx = 0; g.gridwidth = 2;
        p.add(login, g);
        getRootPane().setDefaultButton(login); // Enter key submits

        setContentPane(p);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void doLogin() {
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword());
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.",
                    "Missing Details", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            if (UserDAO.authenticate(user, pass)) {
                dispose();
                new MainFrame(user).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Access Denied: invalid username or password.",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
                passField.setText("");
                passField.requestFocusInWindow();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
