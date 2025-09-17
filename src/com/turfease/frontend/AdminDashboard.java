package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Admin Dashboard - TurfEase");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        int adminId = LoggedInUser.getUserId();
        JLabel title = new JLabel("Welcome, Admin (ID: " + adminId + ")", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10)); // 4 rows for buttons

        JButton addTurf = new JButton("Add New Turf");
        addTurf.addActionListener(e -> {
            frame.dispose();
            new AddTurfPage();
        });

        JButton manageBookings = new JButton("Manage Bookings");
        manageBookings.addActionListener(e -> {
            frame.dispose();
            new ManageBookingsPage();
        });

        JButton selfBooking = new JButton("Self Booking");
        selfBooking.addActionListener(e -> {
            frame.dispose();
            new SelfBookingPage();
        });

        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> {
            LoggedInUser.logout();
            frame.dispose();
            new LoginPage();
        });

        buttonPanel.add(addTurf);
        buttonPanel.add(manageBookings);
        buttonPanel.add(selfBooking);
        buttonPanel.add(logout);

        frame.add(title, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}
