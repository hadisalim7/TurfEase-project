package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AdminDashboard extends JFrame {
    public AdminDashboard() {
        setTitle("Admin Dashboard - TurfEase");
        setSize(700, 450);
        setLocationRelativeTo(null);

        // Don't kill the app when closing this window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //  If minimized → go back to HomePage
        addWindowStateListener(e -> {
            if ((e.getNewState() & Frame.ICONIFIED) == Frame.ICONIFIED) {
                dispose(); 
                new HomePage(); // back to homepage
            }
        });

        setLayout(new BorderLayout());

        // --- Header ---
        int adminId = LoggedInUser.getUserId();
        JLabel title = new JLabel("Welcome, Admin (ID: " + adminId + ")", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        title.setOpaque(true);
        title.setBackground(new Color(0, 102, 0));
        title.setForeground(Color.WHITE);

        // --- Button panel ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 100, 30, 100));
        buttonPanel.setBackground(new Color(245, 245, 245));

        JButton addTurf = new JButton("Add New Turf");
        JButton manageBookings = new JButton("Manage Bookings");
        JButton selfBooking = new JButton("Self Booking");
        JButton logout = new JButton("Logout");

        // style buttons
        JButton[] buttons = {addTurf, manageBookings, selfBooking, logout};
        for (JButton b : buttons) {
            b.setFont(new Font("Segoe UI", Font.BOLD, 18));
            b.setBackground(new Color(34, 139, 34));
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
            b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        }

        // add to panel
        buttonPanel.add(addTurf);
        buttonPanel.add(manageBookings);
        buttonPanel.add(selfBooking);
        buttonPanel.add(logout);

        // add to frame
        add(title, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        // --- Actions ---
        addTurf.addActionListener(e -> {
            if (adminHasTurf(adminId)) {
                JOptionPane.showMessageDialog(this,
                        "Sorry, you already added a turf!",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            } else {
                new AddTurfPage();
            }
        });

        manageBookings.addActionListener(e -> new ManageBookingsPage());
        selfBooking.addActionListener(e -> new SelfBookingPage());

        logout.addActionListener(e -> {
            LoggedInUser.logout();
            dispose();
            new LoginPage();
        });

        setVisible(true);
    }

    // Helper function: check if admin already has turf
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
