package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AdminDashboard extends JFrame {
    public AdminDashboard() {
        setTitle("Admin Dashboard - TurfEase");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        int adminId = LoggedInUser.getUserId();
        JLabel title = new JLabel("Welcome, Admin (ID: " + adminId + ")", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));

        JButton addTurf = new JButton("Add New Turf");
        addTurf.addActionListener(e -> {
            if (adminHasTurf(adminId)) {
                JOptionPane.showMessageDialog(this,
                        "Sorry, you already added a turf!",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            } else {
                new AddTurfPage();
            }
        });

        JButton manageBookings = new JButton("Manage Bookings");
        manageBookings.addActionListener(e -> new ManageBookingsPage());

        JButton selfBooking = new JButton("Self Booking");
        selfBooking.addActionListener(e -> new SelfBookingPage());

        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> {
            LoggedInUser.logout();
            dispose();
            new LoginPage();
        });

        buttonPanel.add(addTurf);
        buttonPanel.add(manageBookings);
        buttonPanel.add(selfBooking);
        buttonPanel.add(logout);

        add(title, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // Helper function: check if this admin already has turf
    private boolean adminHasTurf(int adminId) {
        String url = "jdbc:mysql://localhost:3306/turfease_db";
        String username = "root";
        String password = "hadi123";

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT COUNT(*) FROM turfs WHERE admin_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, adminId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminDashboard::new);
    }
}
