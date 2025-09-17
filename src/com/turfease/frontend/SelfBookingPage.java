package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class SelfBookingPage extends JFrame {
    public SelfBookingPage() {
        setTitle("Self Booking - TurfEase");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(6, 2, 10, 10));

        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField dateField = new JTextField(LocalDate.now().toString());
        JTextField timeField = new JTextField("09:00"); // default

        JButton bookButton = new JButton("Book Slot");

        add(new JLabel("Customer Name:")); add(nameField);
        add(new JLabel("Phone:")); add(phoneField);
        add(new JLabel("Date (YYYY-MM-DD):")); add(dateField);
        add(new JLabel("Start Time (HH:MM):")); add(timeField);
        add(new JLabel()); add(bookButton);

        String url = "jdbc:mysql://localhost:3306/turfease_db";
        String username = "root";
        String password = "hadi123";

        bookButton.addActionListener(e -> {
            String name = nameField.getText();
            String phone = phoneField.getText();
            String date = dateField.getText();
            String time = timeField.getText();

            if (name.isEmpty() || phone.isEmpty() || date.isEmpty() || time.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fill all fields!");
                return;
            }

            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                int turfId = getAdminTurfId(conn);
                if (turfId == 0) {
                    JOptionPane.showMessageDialog(this, "No turf found for this admin!");
                    return;
                }

                int userId = getOrCreateSelfBookingUser(conn, name, phone);
                LocalTime startTime = LocalTime.parse(time);
                LocalTime endTime = startTime.plusHours(1);

                // --------- Availability Check ---------
                String checkQuery = "SELECT COUNT(*) FROM bookings " +
                        "WHERE turf_id=? AND booking_date=? " +
                        "AND start_time < ? AND end_time > ? " +
                        "AND status='booked'";
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                checkStmt.setInt(1, turfId);
                checkStmt.setDate(2, java.sql.Date.valueOf(date));
                checkStmt.setTime(3, java.sql.Time.valueOf(endTime));
                checkStmt.setTime(4, java.sql.Time.valueOf(startTime));
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "Slot already booked!");
                    return;
                }

                // --------- Insert Booking ---------
                String query = "INSERT INTO bookings (user_id, turf_id, booking_date, start_time, end_time, status) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, userId);
                stmt.setInt(2, turfId);
                stmt.setDate(3, java.sql.Date.valueOf(date));
                stmt.setTime(4, java.sql.Time.valueOf(startTime));
                stmt.setTime(5, java.sql.Time.valueOf(endTime));
                stmt.setString(6, "booked");
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Self booking created!");
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        setVisible(true);
    }

    private int getAdminTurfId(Connection conn) throws SQLException {
        String query = "SELECT turf_id FROM turfs WHERE admin_id=? LIMIT 1"; // pick first turf
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, LoggedInUser.getUserId());
        ResultSet rs = stmt.executeQuery();
        return rs.next() ? rs.getInt("turf_id") : 0;
    }

    private int getOrCreateSelfBookingUser(Connection conn, String name, String phone) throws SQLException {
        String check = "SELECT user_id FROM users WHERE phone=?";
        PreparedStatement stmt = conn.prepareStatement(check);
        stmt.setString(1, phone);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("user_id");
        } else {
            String insert = "INSERT INTO users (name, phone) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
            else throw new SQLException("Failed to create self-booking user!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SelfBookingPage::new);
    }
}
