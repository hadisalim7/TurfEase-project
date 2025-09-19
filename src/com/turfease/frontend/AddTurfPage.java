package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddTurfPage extends JFrame {
    public AddTurfPage() {
        setTitle("Add New Turf");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(6, 2, 10, 10));

        JTextField nameField = new JTextField();
        JTextField locationField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField imageField = new JTextField();
        JButton addButton = new JButton("Add Turf");

        add(new JLabel("Turf Name:"));   add(nameField);
        add(new JLabel("Location:"));    add(locationField);
        add(new JLabel("Price per Hour:")); add(priceField);
        add(new JLabel("Image URL/Path:")); add(imageField);
        add(new JLabel()); add(addButton);

        String url = "jdbc:mysql://localhost:3306/turfease_db";
        String username = "root";
        String password = "hadi123";

        // Action when button clicked
        addButton.addActionListener(e -> {
            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                // Check if admin already has a turf
                String checkQuery = "SELECT COUNT(*) FROM turfs WHERE admin_id = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                checkStmt.setInt(1, LoggedInUser.getUserId());
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next() && rs.getInt(1) > 0) {
                    // Show message, but don't close window
                    JOptionPane.showMessageDialog(this,
                            "Sorry, you can't add turf. Each admin is allowed only one turf.",
                            "Warning", JOptionPane.WARNING_MESSAGE);
                    return; // stop here
                }

                // Insert new turf
                String query = "INSERT INTO turfs (turf_name, location, price_per_hour, photo_url, admin_id) " +
                               "VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, nameField.getText());
                stmt.setString(2, locationField.getText());
                stmt.setDouble(3, Double.parseDouble(priceField.getText()));
                stmt.setString(4, imageField.getText());
                stmt.setInt(5, LoggedInUser.getUserId());

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Turf added successfully!");
                dispose(); // close only if added
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AddTurfPage::new);
    }
}
