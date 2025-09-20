package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;

public class HomePage extends JFrame {

    public HomePage() {
        setTitle("TurfEase - Home");
        setSize(700, 400); // matches splash screen size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Background panel with turf image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon bg = new ImageIcon("resources/turf1.jpg"); // football turf image
                g.drawImage(bg.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new GridBagLayout()); // to center card

        // Card Panel (semi-transparent)
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(new Color(255, 255, 255, 200)); // white with transparency
        cardPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel title = new JLabel("Welcome to TurfEase", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        Font btnFont = new Font("Segoe UI", Font.BOLD, 16);
        loginButton.setFont(btnFont);
        registerButton.setFont(btnFont);

        loginButton.setBackground(new Color(34, 139, 34));
        loginButton.setForeground(Color.WHITE);
        registerButton.setBackground(new Color(34, 139, 34));
        registerButton.setForeground(Color.WHITE);

        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add to card panel
        cardPanel.add(title);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        cardPanel.add(loginButton);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        cardPanel.add(registerButton);

        // Place card in center
        backgroundPanel.add(cardPanel, new GridBagConstraints());

        // Add to frame
        add(backgroundPanel);

        // Actions
        loginButton.addActionListener(e -> {
            dispose();
            new LoginPage(); // styled login
        });

        registerButton.addActionListener(e -> {
            dispose();
            new RegisterPage(); // styled register
        });
         loginButton.setFocusPainted(false); // removes focus border
         loginButton.setContentAreaFilled(true); // make sure background fills
         loginButton.setOpaque(true);           // ensure background shows correctly

        setVisible(true);
    }
}