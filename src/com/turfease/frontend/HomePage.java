package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;

public class HomePage {
    public static void main(String[] args) {
        JFrame frame = new JFrame("TurfEase - Home");
        frame.setSize(400, 250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel title = new JLabel("Welcome to TurfEase", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        loginButton.addActionListener(e -> {
            frame.dispose();
            new LoginPage();
        });

        registerButton.addActionListener(e -> {
            frame.dispose();
            new RegisterPage();
        });

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        frame.add(title, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}
