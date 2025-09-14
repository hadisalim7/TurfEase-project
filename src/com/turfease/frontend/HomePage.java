package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;

public class Homepage_test {

    public Homepage_test() {
        createAndShowGUI();
    }

    public void createAndShowGUI() {
        // Main frame
        JFrame frame = new JFrame("TurfEase - Home");
        frame.setSize(600, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Background panel
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon bgIcon = new ImageIcon("path/to/your/image.jpg"); // Replace with your image path
                g.drawImage(bgIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        // Header panel
        JPanel header = new JPanel();
        header.setBackground(new Color(0, 102, 0)); // dark green
        JLabel title = new JLabel("Welcome to TurfEase");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        header.add(title);
        backgroundPanel.add(header, BorderLayout.NORTH);

        // Center panel for buttons
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS)); // vertical buttons
        center.setBorder(BorderFactory.createEmptyBorder(100, 60, 100, 60));
        center.setBackground(new Color(144, 238, 144)); // lighter green

        // Create buttons
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        // Button styles - bigger and bold
        Font btnFont = new Font("Segoe UI", Font.BOLD, 28); // big bold text
        loginButton.setFont(btnFont);
        registerButton.setFont(btnFont);

        Color buttonColor = new Color(34, 139, 34); // green like a button
        loginButton.setBackground(buttonColor);
        loginButton.setForeground(Color.WHITE);
        registerButton.setBackground(buttonColor);
        registerButton.setForeground(Color.WHITE);

        // Left-align buttons
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add spacing between buttons
        center.add(loginButton);
        center.add(Box.createRigidArea(new Dimension(0, 40))); // bigger gap
        center.add(registerButton);

        // Button actions
        loginButton.addActionListener(e -> {
            frame.dispose();
            new LoginPage(); // your existing LoginPage
        });

        registerButton.addActionListener(e -> {
            frame.dispose();
            new RegisterPage(); // your existing RegisterPage
        });

        backgroundPanel.add(center, BorderLayout.CENTER);

        // Footer panel
        JPanel footer = new JPanel();
        footer.setBackground(new Color(189, 195, 199));
        JLabel credits = new JLabel("© 2025 TurfEase Project");
        credits.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        footer.add(credits);
        backgroundPanel.add(footer, BorderLayout.SOUTH);

        frame.add(backgroundPanel);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Homepage_test());
    }
}
