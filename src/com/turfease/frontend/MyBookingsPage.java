package com.turfease.frontend;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MyBookingsPage extends JFrame {
    private DefaultTableModel model;
    private JTable table;
    private JComboBox<String> filterBox;

    public MyBookingsPage() {
        setTitle("My Bookings - TurfEase");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== Header =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(34, 139, 34));

        JLabel title = new JLabel("My Bookings", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.CENTER);

        // Filter dropdown
        String[] filters = {"All", "Upcoming", "Past", "Cancelled"};
        filterBox = new JComboBox<>(filters);
        filterBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        header.add(filterBox, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ===== Table =====
        String[] columns = {"Booking ID", "Turf", "Date", "Start", "End", "Status"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(46, 139, 87));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        // ===== Footer =====
        JPanel footer = new JPanel();
        footer.setBackground(new Color(245, 245, 245));

        JButton cancelBooking = new JButton("Cancel Selected Booking");
        cancelBooking.setBackground(new Color(220, 20, 60));
        cancelBooking.setForeground(Color.WHITE);
        cancelBooking.setFont(new Font("Segoe UI", Font.BOLD, 16));

        footer.add(cancelBooking);
        add(footer, BorderLayout.SOUTH);

        // ===== Load Bookings Initially =====
        loadBookings("All");

        // ===== Actions =====
        filterBox.addActionListener(e -> loadBookings(filterBox.getSelectedItem().toString()));

        cancelBooking.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a booking!");
                return;
            }
            int bookingId = (int) model.getValueAt(row, 0);

            String url = "jdbc:mysql://localhost:3306/turfease_db";
            String username = "root";
            String password = "hadi123";

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

        // Back on Minimize → Return to Dashboard
        addWindowStateListener(e -> {
            if ((e.getNewState() & Frame.ICONIFIED) == Frame.ICONIFIED) { // minimized
                dispose();
                new UserDashboard();
            }
        });

        setVisible(true);
    }

    private void loadBookings(String filter) {
        model.setRowCount(0); // clear table
        String url = "jdbc:mysql://localhost:3306/turfease_db";
        String username = "root";
        String password = "hadi123";

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String baseQuery = "SELECT b.booking_id, t.turf_name, b.booking_date, b.start_time, b.end_time, b.status " +
                               "FROM bookings b JOIN turfs t ON b.turf_id = t.turf_id " +
                               "WHERE b.user_id = ? ";

            // Apply filter
            if (filter.equals("Upcoming")) {
                baseQuery += "AND b.booking_date >= CURDATE() AND b.status='booked' ";
            } else if (filter.equals("Past")) {
                baseQuery += "AND b.booking_date < CURDATE() ";
            } else if (filter.equals("Cancelled")) {
                baseQuery += "AND b.status='cancelled' ";
            }

            baseQuery += "ORDER BY b.booking_date DESC, b.start_time DESC, b.booking_id DESC"; // latest first

            PreparedStatement stmt = conn.prepareStatement(baseQuery);
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
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MyBookingsPage::new);
    }
}
