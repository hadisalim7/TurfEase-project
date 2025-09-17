package com.turfease.frontend;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MyBookingsPage extends JFrame {
    public MyBookingsPage() {
        setTitle("My Bookings - TurfEase");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        String[] columns = {"Booking ID", "Turf", "Date", "Start", "End", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton cancelBooking = new JButton("Cancel Selected Booking");
        add(cancelBooking, BorderLayout.SOUTH);

        String url = "jdbc:mysql://localhost:3306/turfease_db";
        String username = "root";
        String password = "hadi123";

        // Load bookings of logged-in user
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

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MyBookingsPage::new);
    }
}
