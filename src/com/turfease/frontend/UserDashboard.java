package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;

public class UserDashboard extends JFrame {
    public UserDashboard() {
        setTitle("User Dashboard - TurfEase");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Welcome to TurfEase!", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));

        JButton viewTurfs = new JButton("View Turfs");
        viewTurfs.addActionListener(e -> new ViewTurfsPage());

        JButton myBookings = new JButton("My Bookings");
        myBookings.addActionListener(e -> new MyBookingsPage());

        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> {
            LoggedInUser.logout();
            dispose();
            new LoginPage();
        });

        buttonPanel.add(viewTurfs);
        buttonPanel.add(myBookings);
        buttonPanel.add(logout);

        add(title, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(UserDashboard::new);
    }
}
