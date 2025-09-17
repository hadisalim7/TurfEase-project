package com.turfease.frontend;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalTime;

public class ManageBookingsPage extends JFrame {
    public ManageBookingsPage() {
        setTitle("Manage Bookings - TurfEase");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Bookings for Your Turf", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        String[] columnNames = {"Booking ID", "Name", "Phone", "Turf Name", "Date", "Start", "End", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable bookingsTable = new JTable(tableModel);
        add(new JScrollPane(bookingsTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton cancelBooking = new JButton("Cancel Booking");
        JButton editBooking = new JButton("Edit Booking");
        buttonPanel.add(cancelBooking);
        buttonPanel.add(editBooking);
        add(buttonPanel, BorderLayout.SOUTH);

        String url = "jdbc:mysql://localhost:3306/turfease_db";
        String username = "root";
        String password = "hadi123";

        // Load bookings
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
            JOptionPane.showMessageDialog(this, "Error loading bookings: " + ex.getMessage());
        }

        // Cancel booking
        cancelBooking.addActionListener(e -> {
            int row = bookingsTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a booking to cancel!");
                return;
            }
            int bookingId = (int) tableModel.getValueAt(row, 0);
            String currentStatus = tableModel.getValueAt(row, 7).toString();

            if ("cancelled".equalsIgnoreCase(currentStatus)) {
                JOptionPane.showMessageDialog(this, "Already cancelled.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Cancel this booking?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DriverManager.getConnection(url, username, password)) {
                    String update = "UPDATE bookings SET status='cancelled' WHERE booking_id=?";
                    PreparedStatement ps = conn.prepareStatement(update);
                    ps.setInt(1, bookingId);
                    ps.executeUpdate();
                    tableModel.setValueAt("cancelled", row, 7);
                    JOptionPane.showMessageDialog(this, "Booking Cancelled!");
                } catch (Exception ex1) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex1.getMessage());
                }
            }
        });

        // Edit booking
        editBooking.addActionListener(e -> {
            int row = bookingsTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a booking!");
                return;
            }

            int bookingId = (int) tableModel.getValueAt(row, 0);
            String newDate = JOptionPane.showInputDialog(this, "New Date (YYYY-MM-DD):",
                    tableModel.getValueAt(row, 4).toString());
            String newTime = JOptionPane.showInputDialog(this, "New Start Time (HH:MM):",
                    tableModel.getValueAt(row, 5).toString().substring(0, 5));

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

                    tableModel.setValueAt(java.sql.Date.valueOf(newDate), row, 4);
                    tableModel.setValueAt(java.sql.Time.valueOf(start), row, 5);
                    tableModel.setValueAt(java.sql.Time.valueOf(start.plusHours(1)), row, 6);

                    JOptionPane.showMessageDialog(this, "Booking updated!");
                } catch (Exception ex2) {
                    JOptionPane.showMessageDialog(this, "Error updating booking: " + ex2.getMessage());
                }
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ManageBookingsPage::new);
    }
}
