package com.reservation.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Font;

/** Main window shown after login: one tab for booking, one for cancellation. */
public class MainFrame extends JFrame {

    public MainFrame(String username) {
        super("Online Reservation System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel welcome = new JLabel("Logged in as: " + username);
        welcome.setFont(welcome.getFont().deriveFont(Font.BOLD));
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        top.add(welcome, BorderLayout.WEST);
        top.add(logout, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Book Ticket", new ReservationPanel());
        tabs.addTab("Cancel Ticket", new CancellationPanel());

        add(top, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        setSize(560, 520);
        setLocationRelativeTo(null);
    }
}
