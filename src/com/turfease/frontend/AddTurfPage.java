package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddTurfPage {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Add New Turf");
        frame.setSize(400, 350);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(6, 2, 10, 10));

        JTextField nameField = new JTextField();
        JTextField locationField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField imageField = new JTextField();
        JButton addButton = new JButton("Add Turf");

        frame.add(new JLabel("Turf Name:"));
        frame.add(nameField);
        frame.add(new JLabel("Location:"));
        frame.add(locationField);
        frame.add(new JLabel("Price per Hour:"));
        frame.add(priceField);
        frame.add(new JLabel("Image URL/Path:"));
        frame.add(imageField);
        frame.add(new JLabel());
        frame.add(addButton);

        String url = "jdbc:mysql://localhost:3306/turfease_db";
        String username = "root";
        String password = "hadi123";

        // Check if admin already added a turf
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String checkQuery = "SELECT COUNT(*) FROM turfs WHERE admin_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, LoggedInUser.getUserId());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(frame, "You already added a turf. Only one turf is allowed.");
                frame.dispose();
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
        }

        addButton.addActionListener(e -> {
            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                String query = "INSERT INTO turfs (turf_name, location, price_per_hour, photo_url, admin_id) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, nameField.getText());
                stmt.setString(2, locationField.getText());
                stmt.setDouble(3, Double.parseDouble(priceField.getText()));
                stmt.setString(4, imageField.getText());
                stmt.setInt(5, LoggedInUser.getUserId());

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Turf added successfully!");
                frame.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });

        frame.setVisible(true);
    }
}
