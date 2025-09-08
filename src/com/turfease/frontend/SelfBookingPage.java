package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class SelfBookingPage {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Self Booking - TurfEase");
        frame.setSize(400, 350);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(6, 2, 10, 10));

        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField dateField = new JTextField(LocalDate.now().toString());
        JTextField timeField = new JTextField("09:00"); // example default

        JButton bookButton = new JButton("Book Slot");

        frame.add(new JLabel("Customer Name:"));
        frame.add(nameField);
        frame.add(new JLabel("Phone:"));
        frame.add(phoneField);
        frame.add(new JLabel("Date (YYYY-MM-DD):"));
        frame.add(dateField);
        frame.add(new JLabel("Start Time (HH:MM):"));
        frame.add(timeField);
        frame.add(new JLabel());
        frame.add(bookButton);

        String url = "jdbc:mysql://localhost:3306/turfease_db";
        String username = "root";
        String password = "hadi123";

        bookButton.addActionListener(e -> {
            String name = nameField.getText();
            String phone = phoneField.getText();
            String date = dateField.getText();
            String time = timeField.getText();

            if (name.isEmpty() || phone.isEmpty() || date.isEmpty() || time.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Fill all fields!");
                return;
            }

            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                // Insert into bookings with admin as creator
                String query = "INSERT INTO bookings (user_id, turf_id, booking_date, start_time, end_time, status) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, LoggedInUser.getUserId()); // admin id stored here for now
                stmt.setInt(2, getAdminTurfId(conn)); // fetch turf id for logged-in admin
                stmt.setDate(3, java.sql.Date.valueOf(date));
                LocalTime startTime = LocalTime.parse(time);
                stmt.setTime(4, java.sql.Time.valueOf(startTime));
                stmt.setTime(5, java.sql.Time.valueOf(startTime.plusHours(1)));
                stmt.setString(6, "booked");
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(frame, "Self booking created!");
                frame.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });

        frame.setVisible(true);
    }

    private static int getAdminTurfId(Connection conn) throws SQLException {
        String query = "SELECT turf_id FROM turfs WHERE admin_id=?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, LoggedInUser.getUserId());
        ResultSet rs = stmt.executeQuery();
        return rs.next() ? rs.getInt("turf_id") : 0;
    }
}
