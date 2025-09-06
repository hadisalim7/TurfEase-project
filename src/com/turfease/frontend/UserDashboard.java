package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;

public class UserDashboard {
    public static void main(String[] args) {
        JFrame frame = new JFrame("User Dashboard - TurfEase");
        frame.setSize(500, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel title = new JLabel("Welcome to TurfEase!", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        JButton viewTurfs = new JButton("View Turfs");
        viewTurfs.addActionListener(e -> {
        frame.dispose();
     ViewTurfsPage.main(null);
     });

        JButton myBookings = new JButton("My Bookings");
        myBookings.addActionListener(e -> {
        frame.dispose();
        MyBookingsPage.main(null); 
        });

        JButton logout = new JButton("Logout");

        logout.addActionListener(e -> {
            LoggedInUser.logout();
            frame.dispose();
            LoginPage.main(null);
        });

        buttonPanel.add(viewTurfs);
        buttonPanel.add(myBookings);
        buttonPanel.add(logout);

        frame.add(title, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}
