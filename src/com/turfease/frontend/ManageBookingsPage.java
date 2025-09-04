package com.turfease.frontend;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ManageBookingsPage {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Manage Bookings - TurfEase");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel title = new JLabel("Bookings for Your Turf", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(title, BorderLayout.NORTH);

        String[] columnNames = {"Booking ID", "User ID", "Turf Name", "Date", "Start", "End", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable bookingsTable = new JTable(tableModel);
        frame.add(new JScrollPane(bookingsTable), BorderLayout.CENTER);

        String url = "jdbc:mysql://localhost:3306/turfease_db";
        String username = "root";
        String password = "hadi123";

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT b.booking_id, b.user_id, t.turf_name, b.booking_date, b.start_time, b.end_time, b.status " +
                           "FROM bookings b JOIN turfs t ON b.turf_id = t.turf_id " +
                           "WHERE t.admin_id = ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, LoggedInUser.getUserId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("booking_id"),
                        rs.getInt("user_id"),
                        rs.getString("turf_name"),
                        rs.getDate("booking_date"),
                        rs.getTime("start_time"),
                        rs.getTime("end_time"),
                        rs.getString("status")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
        }

        frame.setVisible(true);
    }
}
