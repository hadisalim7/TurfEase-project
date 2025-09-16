package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;

public class HomePage extends JFrame {

    public HomePage() {
        setTitle("TurfEase - Home");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(0, 102, 0));
        JLabel title = new JLabel("Welcome to TurfEase");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Center buttons
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(80, 60, 80, 60));
        center.setBackground(new Color(144, 238, 144));

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        Font btnFont = new Font("Segoe UI", Font.BOLD, 22);
        loginButton.setFont(btnFont);
        registerButton.setFont(btnFont);

        loginButton.setBackground(new Color(34, 139, 34));
        loginButton.setForeground(Color.WHITE);
        registerButton.setBackground(new Color(34, 139, 34));
        registerButton.setForeground(Color.WHITE);

        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(loginButton);
        center.add(Box.createRigidArea(new Dimension(0, 20)));
        center.add(registerButton);

        loginButton.addActionListener(e -> {
            dispose();
            new LoginPage();
        });

        registerButton.addActionListener(e -> {
            dispose();
            new RegisterPage();
        });

        add(center, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel();
        footer.setBackground(new Color(189, 195, 199));
        JLabel credits = new JLabel("© 2025 TurfEase Project");
        credits.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        footer.add(credits);
        add(footer, BorderLayout.SOUTH);

        setVisible(true);
    }
}
