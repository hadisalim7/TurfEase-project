package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;

public class UserDashboard {
    public static void main(String[] args) {
        JFrame frame = new JFrame("User Dashboard - TurfEase");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel title = new JLabel("Welcome to TurfEase!", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));

        JButton viewTurfs = new JButton("View Turfs");
        JButton myBookings = new JButton("My Bookings");
        JButton logout = new JButton("Logout");

        buttonPanel.add(viewTurfs);
        buttonPanel.add(myBookings);
        buttonPanel.add(logout);

        frame.add(title, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);

        logout.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Logging out...");
            frame.dispose(); // Close user window
            LoginPage.main(null); // Back to login page
        });

        frame.setVisible(true);
    }
}
