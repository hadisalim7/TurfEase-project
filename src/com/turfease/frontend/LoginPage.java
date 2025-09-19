package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginPage extends JFrame {

    public LoginPage() {
        setTitle("Login - TurfEase");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2)); // Split into 2 halves

        // Left side (Image panel)
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon img = new ImageIcon("resources/ronaldo1.jpg");
                g.drawImage(img.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };

        // Right side (Form panel)
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(0, 102, 0));
        JLabel title = new JLabel("Login to TurfEase");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(title);
        rightPanel.add(header, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 15)); // added row for back button
        formPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JButton backButton = new JButton("Back"); // new back button

        loginButton.setBackground(new Color(34, 139, 34));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 18));

        backButton.setBackground(new Color(34, 139, 34));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 16));

        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(passLabel);
        formPanel.add(passwordField);
        formPanel.add(new JLabel()); 
        formPanel.add(loginButton);
        formPanel.add(new JLabel()); 
        formPanel.add(backButton); // added to form

        rightPanel.add(formPanel, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel();
        footer.setBackground(new Color(189, 195, 199));
        JLabel credits = new JLabel("© 2025 TurfEase");
        footer.add(credits);
        rightPanel.add(footer, BorderLayout.SOUTH);

        // Add both panels to frame
        add(leftPanel);
        add(rightPanel);

        // Login action
        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/turfease_db", "root", "hadi123");
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT user_id, role FROM users WHERE email=? AND password=?")) {

                stmt.setString(1, email);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String role = rs.getString("role");
                    LoggedInUser.setUser(userId, role);

                    JOptionPane.showMessageDialog(this, "Login Successful!");
                    dispose();
                    if ("admin".equalsIgnoreCase(role)) {
                        AdminDashboard.main(null);
                    } else {
                        UserDashboard.main(null);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid email or password!");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        // Back action
        backButton.addActionListener(e -> {
            dispose();       // close login page
            new HomePage();  // open homepage
        });

        setVisible(true);
    }
}
