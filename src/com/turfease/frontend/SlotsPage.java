package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SlotsPage extends JFrame {
    public SlotsPage(int turfId, String turfName) {
        setTitle("Slots for " + turfName);
        setSize(650, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout());
        JTextField dateField = new JTextField(LocalDate.now().toString(), 10);
        JButton loadSlotsButton = new JButton("Load Slots");
        topPanel.add(new JLabel("Select Date (YYYY-MM-DD):"));
        topPanel.add(dateField);
        topPanel.add(loadSlotsButton);
        add(topPanel, BorderLayout.NORTH);

        JPanel slotPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        add(slotPanel, BorderLayout.CENTER);

        loadSlots(slotPanel, turfId, dateField.getText());

        loadSlotsButton.addActionListener(e ->
                loadSlots(slotPanel, turfId, dateField.getText())
        );

        setVisible(true);
    }

    private void loadSlots(JPanel slotPanel, int turfId, String dateText) {
        slotPanel.removeAll();
        List<LocalTime> bookedSlots = new ArrayList<>();

        String url = "jdbc:mysql://localhost:3306/turfease_db";
        String username = "root";
        String password = "hadi123";

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT start_time FROM bookings WHERE turf_id=? AND booking_date=? AND status='booked'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, turfId);
            stmt.setDate(2, java.sql.Date.valueOf(dateText));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookedSlots.add(rs.getTime("start_time").toLocalTime());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(slotPanel, "Error: " + ex.getMessage());
        }

        for (int hour = 9; hour < 21; hour++) {
            LocalTime slotTime = LocalTime.of(hour, 0);
            JButton slotButton = new JButton(slotTime.toString());

            if (bookedSlots.contains(slotTime)) {
                slotButton.setBackground(Color.RED);
                slotButton.setEnabled(false);
            } else {
                slotButton.setBackground(Color.GREEN);
                slotButton.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(slotPanel,
                            "Book this slot at " + slotTime + " on " + dateText + "?",
                            "Confirm Booking", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        try (Connection conn = DriverManager.getConnection(url, username, password)) {
                            String insert = "INSERT INTO bookings (user_id, turf_id, booking_date, start_time, end_time, status) VALUES (?, ?, ?, ?, ?, ?)";
                            PreparedStatement ps = conn.prepareStatement(insert);
                            ps.setInt(1, LoggedInUser.getUserId());
                            ps.setInt(2, turfId);
                            ps.setDate(3, java.sql.Date.valueOf(dateText));
                            ps.setTime(4, java.sql.Time.valueOf(slotTime));
                            ps.setTime(5, java.sql.Time.valueOf(slotTime.plusHours(1)));
                            ps.setString(6, "booked");
                            ps.executeUpdate();
                            JOptionPane.showMessageDialog(slotPanel, "Booking Confirmed!");
                            loadSlots(slotPanel, turfId, dateText);
                        } catch (Exception ex2) {
                            JOptionPane.showMessageDialog(slotPanel, "Booking Failed: " + ex2.getMessage());
                        }
                    }
                });
            }

            slotPanel.add(slotButton);
        }

        slotPanel.revalidate();
        slotPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SlotsPage(1, "Demo Turf"));
    }
}
