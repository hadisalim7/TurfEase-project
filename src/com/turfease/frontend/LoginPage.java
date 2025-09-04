package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginPage {

    public LoginPage() {
        JFrame frame = new JFrame("Login - TurfEase");
        frame.setSize(400, 250);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(3, 2, 10, 10));

        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        frame.add(new JLabel("Email:"));
        frame.add(emailField);
        frame.add(new JLabel("Password:"));
        frame.add(passwordField);
        frame.add(new JLabel());
        frame.add(loginButton);

        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter both email and password.");
                return;
            }

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

                    // Store the logged-in user
                    LoggedInUser.setUser(userId, role);

                    JOptionPane.showMessageDialog(frame, "Login Successful!");
                    frame.dispose();

                    if ("admin".equalsIgnoreCase(role)) {
                        AdminDashboard.main(null);
                    } else {
                        UserDashboard.main(null);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid email or password!");
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Database Error: " + ex.getMessage());
            }
        });

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new LoginPage();
    }
}
