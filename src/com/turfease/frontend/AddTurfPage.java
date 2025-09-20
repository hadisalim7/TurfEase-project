package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddTurfPage extends JFrame {

    public AddTurfPage() {
        setTitle("Add New Turf - TurfEase");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // center window
        setLayout(new BorderLayout());

        // ===== Header =====
        JPanel header = new JPanel();
        header.setBackground(new Color(34, 153, 84));
        JLabel title = new JLabel("➕ Add New Turf");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // ===== Form Panel =====
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(236, 240, 241));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);

        JLabel nameLbl = new JLabel("Turf Name:");
        nameLbl.setFont(labelFont);
        JTextField nameField = new JTextField();
        nameField.setFont(fieldFont);

        JLabel locationLbl = new JLabel("Location:");
        locationLbl.setFont(labelFont);
        JTextField locationField = new JTextField();
        locationField.setFont(fieldFont);

        JLabel priceLbl = new JLabel("Price per Hour:");
        priceLbl.setFont(labelFont);
        JTextField priceField = new JTextField();
        priceField.setFont(fieldFont);

        JLabel imageLbl = new JLabel("Image URL/Path:");
        imageLbl.setFont(labelFont);
        JTextField imageField = new JTextField();
        imageField.setFont(fieldFont);

        JButton addButton = new JButton("Add Turf");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        addButton.setBackground(new Color(46, 204, 113));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setPreferredSize(new Dimension(150, 40));

        // Add to form with GridBagLayout
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(nameLbl, gbc);
        gbc.gridx = 1; formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(locationLbl, gbc);
        gbc.gridx = 1; formPanel.add(locationField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(priceLbl, gbc);
        gbc.gridx = 1; formPanel.add(priceField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(imageLbl, gbc);
        gbc.gridx = 1; formPanel.add(imageField, gbc);

        gbc.gridx = 1; gbc.gridy = 4; gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(addButton, gbc);

        add(formPanel, BorderLayout.CENTER);

        // ===== Footer =====
        JPanel footer = new JPanel();
        footer.setBackground(new Color(189, 195, 199));
        JLabel credits = new JLabel("© 2025 TurfEase Project");
        credits.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        footer.add(credits);
        add(footer, BorderLayout.SOUTH);

        // ===== Database Logic =====
        String url = "jdbc:mysql://localhost:3306/turfease_db";
        String username = "root";
        String password = "hadi123";

        addButton.addActionListener(e -> {
            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                // Check if admin already has a turf
                String checkQuery = "SELECT COUNT(*) FROM turfs WHERE admin_id = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                checkStmt.setInt(1, LoggedInUser.getUserId());
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Sorry, you can't add turf. Each admin is allowed only one turf.",
                            "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
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
                JOptionPane.showMessageDialog(this, " Turf added successfully!");
                dispose();
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
