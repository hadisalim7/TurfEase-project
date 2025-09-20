package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class SelfBookingPage extends JFrame {

    public SelfBookingPage() {
        setTitle("Self Booking - TurfEase");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== Header =====
        JPanel header = new JPanel();
        header.setBackground(new Color(34, 153, 84));
        JLabel title = new JLabel("Self Booking");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // ===== Form Card =====
        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField dateField = new JTextField(LocalDate.now().toString());
        JTextField timeField = new JTextField("09:00");

        formCard.add(createFieldPanel("Customer Name:", nameField));
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(createFieldPanel("Phone:", phoneField));
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(createFieldPanel("Date (YYYY-MM-DD):", dateField));
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(createFieldPanel("Start Time (HH:MM):", timeField));
        formCard.add(Box.createVerticalStrut(25));

        JButton bookButton = new JButton("Book Slot");
        styleButton(bookButton, new Color(46, 204, 113));
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(bookButton);
        formCard.add(buttonPanel);

        add(formCard, BorderLayout.CENTER);

        // ===== Footer =====
        JPanel footer = new JPanel();
        footer.setBackground(new Color(189, 195, 199));
        JLabel credits = new JLabel("© 2025 TurfEase Project");
        credits.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        footer.add(credits);
        add(footer, BorderLayout.SOUTH);

        // ===== DB Action =====
        String url = "jdbc:mysql://localhost:3306/turfease_db";
        String username = "root";
        String password = "hadi123";

        bookButton.addActionListener(e -> {
            String name = nameField.getText();
            String phone = phoneField.getText();
            String date = dateField.getText();
            String time = timeField.getText();

            if (name.isEmpty() || phone.isEmpty() || date.isEmpty() || time.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!");
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

                // Check slot availability
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

                // Insert booking
                String query = "INSERT INTO bookings (user_id, turf_id, booking_date, start_time, end_time, status) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, userId);
                stmt.setInt(2, turfId);
                stmt.setDate(3, java.sql.Date.valueOf(date));
                stmt.setTime(4, java.sql.Time.valueOf(startTime));
                stmt.setTime(5, java.sql.Time.valueOf(endTime));
                stmt.setString(6, "booked");
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, " Self booking created successfully!");
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "error: " + ex.getMessage());
            }
        });

        setVisible(true);
    }

    private JPanel createFieldPanel(String labelText, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setPreferredSize(new Dimension(250, 35));
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private void styleButton(JButton btn, Color bgColor) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private int getAdminTurfId(Connection conn) throws SQLException {
        String query = "SELECT turf_id FROM turfs WHERE admin_id=? LIMIT 1";
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
        if (rs.next()) return rs.getInt("user_id");
        else {
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
