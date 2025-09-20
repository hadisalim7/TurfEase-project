package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SlotsPage extends JFrame {
    private int turfId;

    public SlotsPage(int turfId, String turfName) {
        this.turfId = turfId;

        setTitle("Slots for " + turfName);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout());

        // Top panel with date selector
        JPanel topPanel = new JPanel(new FlowLayout());
        JTextField dateField = new JTextField(LocalDate.now().toString(), 10);
        JButton loadSlotsButton = new JButton("Load Slots");
        JButton backButton = new JButton("⬅ Back");

        backButton.addActionListener(e -> {
            dispose();
            new ViewTurfsPage(); // go back
        });

        topPanel.add(backButton);
        topPanel.add(new JLabel("Date:"));
        topPanel.add(dateField);
        topPanel.add(loadSlotsButton);
        add(topPanel, BorderLayout.NORTH);

        // Slots grid
        JPanel slotPanel = new JPanel(new GridLayout(3, 4, 15, 15));
        add(slotPanel, BorderLayout.CENTER);

        // Load slots initially
        loadSlots(slotPanel, this.turfId, dateField.getText());

        // Reload when button clicked
        loadSlotsButton.addActionListener(e ->
                loadSlots(slotPanel, this.turfId, dateField.getText())
        );

        setVisible(true);
    }

    private void loadSlots(JPanel slotPanel, int turfId, String dateText) {
        slotPanel.removeAll();
        List<LocalTime> bookedSlots = new ArrayList<>();

        String url = "jdbc:mysql://localhost:3306/turfease_db";
        String username = "root";
        String password = "hadi123";

        LocalDate selectedDate = LocalDate.parse(dateText);
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // Get booked slots from DB
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT start_time FROM bookings WHERE turf_id=? AND booking_date=? AND status='booked'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, turfId);
            stmt.setDate(2, java.sql.Date.valueOf(selectedDate));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookedSlots.add(rs.getTime("start_time").toLocalTime());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(slotPanel, "Error: " + ex.getMessage());
        }

        // Create slot buttons (9 AM - 9 PM)
        for (int hour = 9; hour < 21; hour++) {
            LocalTime slotTime = LocalTime.of(hour, 0);
            String displayTime = (hour <= 12) ? hour + " AM" : (hour - 12) + " PM";

            JButton slotButton = new JButton(displayTime);
            slotButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            slotButton.setOpaque(true);
            slotButton.setBorderPainted(false);

            boolean isPast = selectedDate.isBefore(today) ||
                    (selectedDate.isEqual(today) && slotTime.isBefore(now));

            if (isPast) {
                // Already over
                slotButton.setBackground(Color.GRAY);
                slotButton.setForeground(Color.WHITE);
                slotButton.setEnabled(false);
            } else if (bookedSlots.contains(slotTime)) {
                // Booked slot
                slotButton.setBackground(Color.RED);
                slotButton.setForeground(Color.WHITE);
                slotButton.setEnabled(false);
            } else {
                // Available slot
                slotButton.setBackground(new Color(34, 139, 34));
                slotButton.setForeground(Color.WHITE);

                slotButton.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(slotPanel,
                            "Book this slot at " + displayTime + " on " + dateText + "?",
                            "Confirm Booking", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        try (Connection conn = DriverManager.getConnection(url, username, password)) {
                            String insert = "INSERT INTO bookings (user_id, turf_id, booking_date, start_time, end_time, status) VALUES (?, ?, ?, ?, ?, ?)";
                            PreparedStatement ps = conn.prepareStatement(insert);
                            ps.setInt(1, LoggedInUser.getUserId());
                            ps.setInt(2, turfId);
                            ps.setDate(3, java.sql.Date.valueOf(selectedDate));
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
