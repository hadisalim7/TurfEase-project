package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Admin Dashboard - TurfEase");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel title = new JLabel("Welcome, Admin!", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));

        JButton addTurf = new JButton("Add New Turf");
        JButton manageBookings = new JButton("Manage Bookings");
        JButton logout = new JButton("Logout");

        buttonPanel.add(addTurf);
        buttonPanel.add(manageBookings);
        buttonPanel.add(logout);

        frame.add(title, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);

        logout.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Logging out...");
            frame.dispose(); // Close admin window
            LoginPage.main(null); // Back to login page
        });

        frame.setVisible(true);
    }
}
