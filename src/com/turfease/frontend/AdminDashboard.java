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

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));

        JButton addTurf = new JButton("Add New Turf");
        addTurf.addActionListener(e -> AddTurfPage.main(null));

        JButton manageBookings = new JButton("Manage Bookings");
        manageBookings.addActionListener(e -> ManageBookingsPage.main(null));

        JButton selfBooking = new JButton("Self Booking");
        selfBooking.addActionListener(e -> SelfBookingPage.main(null));
        buttonPanel.add(selfBooking);


        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> {
            LoggedInUser.logout();
            frame.dispose();
            LoginPage.main(null);
        });

        buttonPanel.add(addTurf);
        buttonPanel.add(manageBookings);
        buttonPanel.add(logout);

        frame.add(title, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}
