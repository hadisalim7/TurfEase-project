package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SlotsPage {
    public static void main(String[] args) {
        if (args.length < 2) return;

        int turfId = Integer.parseInt(args[0]);
        String turfName = args[1];

        JFrame frame = new JFrame("Slots for " + turfName);
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel slotPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        frame.add(slotPanel, BorderLayout.CENTER);

        // Date selection (default today)
        LocalDate date = LocalDate.now();

        List<LocalTime> bookedSlots = new ArrayList<>();
        String url = "jdbc:mysql://localhost:3306/turfease_db";
        String username = "root";
        String password = "hadi123";

        // Fetch booked slots
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT start_time FROM bookings WHERE turf_id=? AND booking_date=? AND status='booked'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, turfId);
            stmt.setDate(2, java.sql.Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookedSlots.add(rs.getTime("start_time").toLocalTime());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
        }

        // Create 12 slots from 9AM–9PM
        for (int hour = 9; hour < 21; hour++) {
            LocalTime slotTime = LocalTime.of(hour, 0);
            JButton slotButton = new JButton(slotTime.toString());

            if (bookedSlots.contains(slotTime)) {
                slotButton.setBackground(Color.RED);
                slotButton.setEnabled(false);
            } else {
                slotButton.setBackground(Color.GREEN);
                slotButton.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(frame,
                            "Book this slot at " + slotTime + "?",
                            "Confirm Booking", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        try (Connection conn = DriverManager.getConnection(url, username, password)) {
                            String insert = "INSERT INTO bookings (user_id, turf_id, booking_date, start_time, end_time, status) VALUES (?, ?, ?, ?, ?, ?)";
                            PreparedStatement ps = conn.prepareStatement(insert);
                            ps.setInt(1, LoggedInUser.getUserId());
                            ps.setInt(2, turfId);
                            ps.setDate(3, java.sql.Date.valueOf(date));
                            ps.setTime(4, java.sql.Time.valueOf(slotTime));
                            ps.setTime(5, java.sql.Time.valueOf(slotTime.plusHours(1)));
                            ps.setString(6, "booked");
                            ps.executeUpdate();
                            JOptionPane.showMessageDialog(frame, "Booking Confirmed!");
                            frame.dispose();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(frame, "Booking Failed: " + ex.getMessage());
                        }
                    }
                });
            }

            slotPanel.add(slotButton);
        }

        frame.setVisible(true);
    }
}

