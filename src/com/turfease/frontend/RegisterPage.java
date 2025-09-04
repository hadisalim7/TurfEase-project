package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegisterPage extends JFrame {
    public RegisterPage() {
        setTitle("Register Page");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 2, 10, 10));

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField phoneField = new JTextField();
        JButton registerButton = new JButton("Register");

        add(new JLabel("Name:")); add(nameField);
        add(new JLabel("Email:")); add(emailField);
        add(new JLabel("Password:")); add(passwordField);
        add(new JLabel("Phone:")); add(phoneField);
        add(new JLabel()); add(registerButton);

        registerButton.addActionListener(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String phone = phoneField.getText();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Fill all fields");
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

                JOptionPane.showMessageDialog(null, "Registration Successful!");
                dispose();
                new LoginPage();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Database Error!");
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new RegisterPage();
    }
}
