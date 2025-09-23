package com.turfease.frontend;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ManageBookingsPage extends JFrame {

    public ManageBookingsPage() {
        setTitle("Manage Bookings - TurfEase");
        setSize(950, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== Header =====
        JPanel header = new JPanel();
        header.setBackground(new Color(34, 153, 84));
        JLabel title = new JLabel("Manage Bookings");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // ===== Table =====
        String[] columnNames = {"Booking ID", "Name", "Phone", "Turf ID", "Turf Name", "Date", "Start", "End", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable bookingsTable = new JTable(tableModel);
        bookingsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bookingsTable.setRowHeight(28);
        bookingsTable.getTableHeader().setBackground(new Color(34, 153, 84));
        bookingsTable.getTableHeader().setForeground(Color.WHITE);
        bookingsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Alternate row colors
        bookingsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(230, 245, 233));
                }
                return c;
            }
        });

        add(new JScrollPane(bookingsTable), BorderLayout.CENTER);

        // ===== Buttons =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        buttonPanel.setBackground(new Color(236, 240, 241));

        JButton cancelBooking = createButton("Cancel Booking", new Color(231, 76, 60));
        JButton editBooking = createButton("Edit Booking", new Color(46, 204, 113));

        buttonPanel.add(cancelBooking);
        buttonPanel.add(editBooking);
        add(buttonPanel, BorderLayout.SOUTH);

        // ===== Database =====
        String url = "jdbc:mysql://localhost:3306/turfease_db";
        String username = "root";
        String password = "hadi123";

        // Load bookings
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT b.booking_id, u.name, u.phone, t.turf_id, t.turf_name, b.booking_date, " +
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
                        rs.getInt("turf_id"),
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

        // ===== Cancel Booking Action =====
        cancelBooking.addActionListener(e -> {
            int row = bookingsTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a booking to cancel!");
                return;
            }
            int bookingId = (int) tableModel.getValueAt(row, 0);
            String currentStatus = tableModel.getValueAt(row, 8).toString();

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
                    tableModel.setValueAt("cancelled", row, 8);
                    JOptionPane.showMessageDialog(this, "Booking Cancelled!");
                } catch (Exception ex1) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex1.getMessage());
                }
            }
        });

        // ===== Edit Booking Action =====
        editBooking.addActionListener(e -> {
            int row = bookingsTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a booking!");
                return;
            }

            int bookingId = (int) tableModel.getValueAt(row, 0);
            int turfId = (int) tableModel.getValueAt(row, 3);
            String newDate = JOptionPane.showInputDialog(this, "New Date (YYYY-MM-DD):",
                    tableModel.getValueAt(row, 5).toString());
            String newTime = JOptionPane.showInputDialog(this, "New Start Time (HH:MM):",
                    tableModel.getValueAt(row, 6).toString().substring(0, 5));

            if (newDate != null && newTime != null) {
                try (Connection conn = DriverManager.getConnection(url, username, password)) {
                    java.time.LocalTime start = java.time.LocalTime.parse(newTime);

                    // ===== Check if the new slot is already booked =====
                    String checkQuery = "SELECT COUNT(*) FROM bookings WHERE turf_id=? AND booking_date=? " +
                            "AND start_time=? AND status='booked' AND booking_id<>?";
                    PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                    checkStmt.setInt(1, turfId);
                    checkStmt.setDate(2, java.sql.Date.valueOf(newDate));
                    checkStmt.setTime(3, java.sql.Time.valueOf(start));
                    checkStmt.setInt(4, bookingId);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(this, "This slot is already booked! Choose another time.");
                        return;
                    }

                    // ===== If slot free, update booking =====
                    String update = "UPDATE bookings SET booking_date=?, start_time=?, end_time=? WHERE booking_id=?";
                    PreparedStatement ps = conn.prepareStatement(update);
                    ps.setDate(1, java.sql.Date.valueOf(newDate));
                    ps.setTime(2, java.sql.Time.valueOf(start));
                    ps.setTime(3, java.sql.Time.valueOf(start.plusHours(1)));
                    ps.setInt(4, bookingId);
                    ps.executeUpdate();

                    tableModel.setValueAt(java.sql.Date.valueOf(newDate), row, 5);
                    tableModel.setValueAt(java.sql.Time.valueOf(start), row, 6);
                    tableModel.setValueAt(java.sql.Time.valueOf(start.plusHours(1)), row, 7);

                    JOptionPane.showMessageDialog(this, "Booking updated!");
                } catch (Exception ex2) {
                    JOptionPane.showMessageDialog(this, "Error updating booking: " + ex2.getMessage());
                }
            }
        });

        // ===== Footer =====
        JPanel footer = new JPanel();
        footer.setBackground(new Color(189, 195, 199));
        JLabel credits = new JLabel("© 2025 TurfEase Project");
        credits.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        footer.add(credits);
        add(footer, BorderLayout.NORTH);

        setVisible(true);
    }

    // ===== Helper to create styled buttons =====
    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(160, 40));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ManageBookingsPage::new);
    }
}
