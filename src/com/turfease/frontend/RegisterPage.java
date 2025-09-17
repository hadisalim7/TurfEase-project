package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterPage extends JFrame {
    public RegisterPage() {
        setTitle("Register - TurfEase");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2)); // Two halves

        // Left side (Image panel)
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon img = new ImageIcon("resources/coach1.jpeg"); 
                g.drawImage(img.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };

        // Right side (Form panel)
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(0, 102, 0));
        JLabel title = new JLabel("Create Your Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(title);
        rightPanel.add(header, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel nameLabel = new JLabel("Full Name:");
        JTextField nameField = new JTextField();
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        JLabel phoneLabel = new JLabel("Phone:");
        JTextField phoneField = new JTextField();

        JButton registerButton = new JButton("Register");
        registerButton.setBackground(new Color(34, 139, 34));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 18));

        formPanel.add(nameLabel); formPanel.add(nameField);
        formPanel.add(emailLabel); formPanel.add(emailField);
        formPanel.add(passLabel); formPanel.add(passwordField);
        formPanel.add(phoneLabel); formPanel.add(phoneField);
        formPanel.add(new JLabel()); formPanel.add(registerButton);

        rightPanel.add(formPanel, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel();
        footer.setBackground(new Color(189, 195, 199));
        JLabel credits = new JLabel("© 2025 TurfEase");
        footer.add(credits);
        rightPanel.add(footer, BorderLayout.SOUTH);

        // Add both panels
        add(leftPanel);
        add(rightPanel);

        // Action
        registerButton.addActionListener(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String phone = phoneField.getText();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!");
                return;
            }

            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/turfease_db", "root", "hadi123");
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO users (name, email, password, phone) VALUES (?, ?, ?, ?)")) {

                stmt.setString(1, name);
                stmt.setString(2, email);
                stmt.setString(3, password);
                stmt.setString(4, phone);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Registration Successful!");
                dispose();
                new LoginPage();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        setVisible(true);
    }
}
