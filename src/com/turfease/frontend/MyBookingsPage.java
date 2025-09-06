package com.turfease.frontend;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MyBookingsPage {
    public static void main(String[] args) {
        JFrame frame = new JFrame("My Bookings - TurfEase");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        String[] columnNames = {"Booking ID", "Turf", "Date", "Start", "End", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton cancelBooking = new JButton("Cancel Selected Booking");
        frame.add(cancelBooking, BorderLayout.SOUTH);

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
            JOptionPane.showMessageDialog(frame, "Database Error: " + e.getMessage());
        }

        cancelBooking.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "Please select a booking to cancel!");
                return;
            }
            int bookingId = (int) model.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(frame, "Cancel this booking?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DriverManager.getConnection(url, username, password)) {
                    String update = "UPDATE bookings SET status='cancelled' WHERE booking_id=?";
                    PreparedStatement ps = conn.prepareStatement(update);
                    ps.setInt(1, bookingId);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(frame, "Booking Cancelled!");
                    frame.dispose();
                    main(null); // reload page
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Cancel Failed: " + ex.getMessage());
                }
            }
        });

        frame.setVisible(true);
    }
}

