package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AdminDashboard extends JFrame {

    public AdminDashboard() {
        setTitle("Admin Dashboard - TurfEase");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center on screen
        setLayout(new BorderLayout());

        // ===== Header =====
        JPanel header = new JPanel();
        header.setBackground(new Color(34, 153, 84));
        JLabel title = new JLabel("👑 Admin Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // ===== Button Panel =====
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(new Color(236, 240, 241));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton addTurf = createButton("Add New Turf", new Color(46, 204, 113));
        addTurf.addActionListener(e -> {
            int adminId = LoggedInUser.getUserId();
            if (adminHasTurf(adminId)) {
                JOptionPane.showMessageDialog(this,
                        "Sorry, you already added a turf!",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            } else {
                new AddTurfPage();
            }
        });

        JButton manageBookings = createButton("Manage Bookings", new Color(52, 152, 219));
        manageBookings.addActionListener(e -> new ManageBookingsPage());

        JButton selfBooking = createButton("Self Booking", new Color(241, 196, 15));
        selfBooking.addActionListener(e -> new SelfBookingPage());

        JButton logout = createButton("Logout", new Color(231, 76, 60));
        logout.addActionListener(e -> {
            LoggedInUser.logout();
            dispose();
            new LoginPage();
        });

        gbc.gridy = 0; buttonPanel.add(addTurf, gbc);
        gbc.gridy = 1; buttonPanel.add(manageBookings, gbc);
        gbc.gridy = 2; buttonPanel.add(selfBooking, gbc);
        gbc.gridy = 3; buttonPanel.add(logout, gbc);

        add(buttonPanel, BorderLayout.CENTER);

        // ===== Footer =====
        JPanel footer = new JPanel();
        footer.setBackground(new Color(189, 195, 199));
        JLabel credits = new JLabel("© 2025 TurfEase Project");
        credits.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        footer.add(credits);
        add(footer, BorderLayout.SOUTH);

        setVisible(true);
    }

    // ===== Helper to style buttons =====
    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(250, 50));
        return btn;
    }

    // ===== Check if admin already has a turf =====
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
