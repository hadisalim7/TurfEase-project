package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SlotsPage extends JFrame {

    public SlotsPage(int turfId, String turfName) {
        setTitle("Slots for " + turfName);
        setSize(750, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 10));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        // ===== Header =====
        JPanel header = new JPanel();
        header.setBackground(new Color(34, 153, 84));
        JLabel title = new JLabel("Available Slots for " + turfName);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // ===== Top panel for date selection =====
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(236, 240, 241));
        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JLabel dateLabel = new JLabel("Select Date (DD-MM-YYYY):");
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JTextField dateField = new JTextField(LocalDate.now().format(formatter), 10);
        dateField.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JButton loadSlotsButton = new JButton("Load Slots");
        loadSlotsButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loadSlotsButton.setBackground(new Color(46, 204, 113));
        loadSlotsButton.setForeground(Color.WHITE);
        loadSlotsButton.setFocusPainted(false);

        topPanel.add(dateLabel);
        topPanel.add(dateField);
        topPanel.add(loadSlotsButton);
        add(topPanel, BorderLayout.NORTH);

        // ===== Slots panel =====
        JPanel slotPanel = new JPanel(new GridLayout(3, 4, 20, 20));
        slotPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        slotPanel.setBackground(new Color(236, 240, 241));
        add(slotPanel, BorderLayout.CENTER);

        // Load initial slots
        loadSlots(slotPanel, turfId, dateField.getText(), formatter);

        loadSlotsButton.addActionListener(e ->
                loadSlots(slotPanel, turfId, dateField.getText(), formatter)
        );

        setVisible(true);
    }

    private void loadSlots(JPanel slotPanel, int turfId, String dateText, DateTimeFormatter formatter) {
        slotPanel.removeAll();

        LocalDate selectedDate;
        try {
            selectedDate = LocalDate.parse(dateText, formatter);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(slotPanel, "Invalid date format! Use DD-MM-YYYY");
            return;
        }

        if (selectedDate.isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(slotPanel, "Cannot view slots for past dates!");
            return;
        }

        List<LocalTime> bookedSlots = new ArrayList<>();
        String url = "jdbc:mysql://localhost:3306/turfease_db";
        String username = "root";
        String password = "hadi123";

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

        for (int hour = 9; hour < 21; hour++) {
            LocalTime slotTime = LocalTime.of(hour, 0);
            String slotText = slotTime + " - " + slotTime.plusHours(1);
            JButton slotButton = new JButton(slotText);
            slotButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
            slotButton.setOpaque(true);
            slotButton.setFocusPainted(false);

            if (bookedSlots.contains(slotTime)) {
                // Booked slot style
                slotButton.setBackground(new Color(231, 76, 60));
                slotButton.setEnabled(false);
                slotButton.setForeground(Color.WHITE);
                slotButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
            } else {
                // Available slot style
                slotButton.setBackground(new Color(46, 204, 113));
                slotButton.setForeground(Color.WHITE);
                slotButton.setBorder(BorderFactory.createRaisedBevelBorder());

                // Hover effect
                slotButton.addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent evt) {
                        slotButton.setBackground(new Color(39, 174, 96));
                    }

                    public void mouseExited(MouseEvent evt) {
                        slotButton.setBackground(new Color(46, 204, 113));
                    }
                });

                slotButton.addActionListener(e -> {
                    if (selectedDate.equals(LocalDate.now()) && slotTime.isBefore(LocalTime.now())) {
                        JOptionPane.showMessageDialog(slotPanel, "Cannot book past time slots!");
                        return;
                    }

                    int confirm = JOptionPane.showConfirmDialog(slotPanel,
                            "Book this slot at " + slotText + " on " + dateText + "?",
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
                            loadSlots(slotPanel, turfId, dateText, formatter);
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
