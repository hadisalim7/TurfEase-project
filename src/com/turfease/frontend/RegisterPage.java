package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterPage extends JFrame {

    // Declare form components
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField phoneField;
    private JButton registerButton;
    private JButton backButton;

    public RegisterPage() {
        // Window title
        setTitle("TurfEase - Register");
        setSize(350, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center the window

        // Main panel layout
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2, 10, 10)); // rows, cols, hgap, vgap
        add(panel);

        // Labels and Fields
        panel.add(new JLabel("Name:"));
        nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Email:"));
        emailField = new JTextField();
        panel.add(emailField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        panel.add(new JLabel("Phone:"));
        phoneField = new JTextField();
        panel.add(phoneField);

        // Register button
        registerButton = new JButton("Register");
        panel.add(registerButton);

        // Back button
        backButton = new JButton("Back to Login");
        panel.add(backButton);

        // Register button action
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                String phone = phoneField.getText();

                // For now, just show entered data
                JOptionPane.showMessageDialog(null,
                        "Registered Successfully!\nName: " + name +
                                "\nEmail: " + email +
                                "\nPhone: " + phone);
            }
        });

        // Back button action
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close current window
                new LoginPage().setVisible(true); // Open login page
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RegisterPage().setVisible(true);
        });
    }
}
