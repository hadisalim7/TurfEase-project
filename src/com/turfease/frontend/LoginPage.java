package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage {
    public static void main(String[] args) {
        JFrame frame = new JFrame("TurfEase Login");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(4, 2));

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        frame.add(emailLabel);
        frame.add(emailField);
        frame.add(passwordLabel);
        frame.add(passwordField);
        frame.add(new JLabel()); // Empty for spacing
        frame.add(loginButton);

        // Action on button click
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                try (Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/turfease_db", "root", "hadi123")) {

                    String query = "SELECT role FROM users WHERE email=? AND password=?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, email);
                    stmt.setString(2, password);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        String role = rs.getString("role");
                        if (role.equals("admin")) {
                            JOptionPane.showMessageDialog(frame, "Welcome Admin!");
                            AdminDashboard.main(null);
                            frame.dispose(); // close login window

                        } else {
                            JOptionPane.showMessageDialog(frame, "Welcome User!");
                            if (role.equals("admin")) {
                                AdminDashboard.main(null);
                            } else {
                            UserDashboard.main(null);
                            }
                            frame.dispose();

                            }
                    } else {
                        JOptionPane.showMessageDialog(frame, "Invalid email or password!");
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Database connection error!");
                }
            }
        });

        frame.setVisible(true);
    }
}
