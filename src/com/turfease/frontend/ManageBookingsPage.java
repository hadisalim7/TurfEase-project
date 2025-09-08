package com.turfease.frontend;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalTime;

public class ManageBookingsPage {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Manage Bookings - TurfEase");
        frame.setSize(900, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel title = new JLabel("Bookings for Your Turf", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(title, BorderLayout.NORTH);

        String[] columnNames = {"Booking ID", "Name", "Phone", "Turf Name", "Date", "Start", "End", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable bookingsTable = new JTable(tableModel);
        frame.add(new JScrollPane(bookingsTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton cancelBooking = new JButton("Cancel Booking");
        JButton editBooking = new JButton("Edit Booking");
        buttonPanel.add(cancelBooking);
        buttonPanel.add(editBooking);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        String url = "jdbc:mysql://localhost:3306/turfease_db";
        String username = "root";
        String password = "hadi123";

        // Load existing bookings with user details
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT b.booking_id, u.name, u.phone, t.turf_name, b.booking_date, " +
                           "b.start_time, b.end_time, b.status " +
                           "FROM bookings b " +
                           "JOIN turfs t ON b.turf_id = t.turf_id " +
                           "JOIN users u ON b.user_id = u.user_id " +
                           "WHERE t.admin_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, LoggedInUser.getUserId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("booking_id"),
                        rs.getString("name"),
                        rs.getString("phone"),
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

        // Cancel booking
        cancelBooking.addActionListener(e -> {
            int row = bookingsTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "Please select a booking to cancel!");
                return;
            }

            int bookingId = (int) tableModel.getValueAt(row, 0);
            String currentStatus = tableModel.getValueAt(row, 7).toString();

            if ("cancelled".equalsIgnoreCase(currentStatus)) {
                JOptionPane.showMessageDialog(frame, "This booking is already cancelled.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to cancel this booking?",
                    "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DriverManager.getConnection(url, username, password)) {
                    String update = "UPDATE bookings SET status='cancelled' WHERE booking_id=?";
                    PreparedStatement ps = conn.prepareStatement(update);
                    ps.setInt(1, bookingId);
                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(frame, "Booking Cancelled!");
                    tableModel.setValueAt("cancelled", row, 7);
                } catch (SQLException ex1) {
                    JOptionPane.showMessageDialog(frame, "Error cancelling booking: " + ex1.getMessage());
                }
            }
        });

        // Edit booking
        editBooking.addActionListener(e -> {
            int row = bookingsTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "Please select a booking to edit!");
                return;
            }

            int bookingId = (int) tableModel.getValueAt(row, 0);
            String newDate = JOptionPane.showInputDialog(frame, "Enter new date (YYYY-MM-DD):",
                    tableModel.getValueAt(row, 4).toString());
            String newTime = JOptionPane.showInputDialog(frame, "Enter new start time (HH:MM):",
                    tableModel.getValueAt(row, 5).toString().substring(0,5));

            if (newDate != null && newTime != null) {
                try (Connection conn = DriverManager.getConnection(url, username, password)) {
                    String update = "UPDATE bookings SET booking_date=?, start_time=?, end_time=? WHERE booking_id=?";
                    PreparedStatement ps = conn.prepareStatement(update);
                    ps.setDate(1, java.sql.Date.valueOf(newDate));
                    LocalTime start = LocalTime.parse(newTime);
                    ps.setTime(2, java.sql.Time.valueOf(start));
                    ps.setTime(3, java.sql.Time.valueOf(start.plusHours(1)));
                    ps.setInt(4, bookingId);
                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(frame, "Booking updated!");
                    tableModel.setValueAt(java.sql.Date.valueOf(newDate), row, 4);
                    tableModel.setValueAt(java.sql.Time.valueOf(start), row, 5);
                    tableModel.setValueAt(java.sql.Time.valueOf(start.plusHours(1)), row, 6);
                } catch (SQLException ex2) {
                    JOptionPane.showMessageDialog(frame, "Error updating booking: " + ex2.getMessage());
                }
            }
        });

        frame.setVisible(true);
    }
}
