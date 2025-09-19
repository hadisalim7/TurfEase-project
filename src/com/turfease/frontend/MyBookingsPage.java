package com.turfease.frontend;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;

public class MyBookingsPage extends JFrame {
    public MyBookingsPage() {
        setTitle("My Bookings - TurfEase");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Background with turf image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon bg = new ImageIcon("resources/turf1.jpg");
                g.drawImage(bg.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        // Transparent card panel
        JPanel cardPanel = new JPanel(new BorderLayout(10, 10));
        cardPanel.setBackground(new Color(255, 255, 255, 220));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Table setup
        String[] columns = {"Booking ID", "Turf", "Date", "Start", "End", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // disable editing
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(new Color(34, 139, 34));
        header.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        cardPanel.add(scrollPane, BorderLayout.CENTER);

        // Cancel button
        JButton cancelBooking = new JButton("❌ Cancel Selected Booking");
        cancelBooking.setBackground(new Color(220, 53, 69));
        cancelBooking.setForeground(Color.WHITE);
        cancelBooking.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cancelBooking.setFocusPainted(false);
        cardPanel.add(cancelBooking, BorderLayout.SOUTH);

        // Load bookings from DB
        String url = "jdbc:mysql://localhost:3306/turfease_db";
        String username = "root";
        String password = "hadi123";

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT b.booking_id, t.turf_name, b.booking_date, b.start_time, b.end_time, b.status " +
                    "FROM bookings b JOIN turfs t ON b.turf_id = t.turf_id " +
                    "WHERE b.user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, LoggedInUser.getUserId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("booking_id"),
                        rs.getString("turf_name"),
                        rs.getDate("booking_date"),
                        rs.getTime("start_time"),
                        rs.getTime("end_time"),
                        rs.getString("status")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }

        // Cancel booking action
        cancelBooking.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a booking!");
                return;
            }
            int bookingId = (int) model.getValueAt(row, 0);

            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                String update = "UPDATE bookings SET status='cancelled' WHERE booking_id=?";
                PreparedStatement ps = conn.prepareStatement(update);
                ps.setInt(1, bookingId);
                ps.executeUpdate();

                model.setValueAt("cancelled", row, 5);
                JOptionPane.showMessageDialog(this, "Booking Cancelled!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Cancel Failed: " + ex.getMessage());
            }
        });

        backgroundPanel.add(cardPanel, BorderLayout.CENTER);
        add(backgroundPanel);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MyBookingsPage::new);
    }
}