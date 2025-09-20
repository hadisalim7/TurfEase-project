package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;

public class UserDashboard extends JFrame {
    public UserDashboard() {
        setTitle("User Dashboard - TurfEase");
        setSize(600, 400); // make same size as homepage
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Background panel with image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon bg = new ImageIcon("resources/turf1.jpg"); // same turf background
                g.drawImage(bg.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new GridBagLayout()); // center content

        // Card Panel (semi-transparent)
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(new Color(255, 255, 255, 220)); // white with transparency
        cardPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Title
        JLabel title = new JLabel("Welcome to TurfEase, User!", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Buttons
        JButton viewTurfs = new JButton("View Turfs");
        JButton myBookings = new JButton("My Bookings");
        JButton logout = new JButton("Logout");

        Font btnFont = new Font("Segoe UI", Font.BOLD, 16);
        for (JButton btn : new JButton[]{viewTurfs, myBookings, logout}) {
            btn.setFont(btnFont);
            btn.setBackground(new Color(34, 139, 34)); // green
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(200, 40));
        }

        // Actions
        viewTurfs.addActionListener(e -> {
            dispose();
            new ViewTurfsPage();
        });

        myBookings.addActionListener(e -> {
            dispose();
            new MyBookingsPage();
        });

        logout.addActionListener(e -> {
            LoggedInUser.logout();
            dispose();
            new LoginPage();
        });

        // Add to card panel
        cardPanel.add(title);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        cardPanel.add(viewTurfs);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        cardPanel.add(myBookings);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        cardPanel.add(logout);

        // Place card at center
        backgroundPanel.add(cardPanel, new GridBagConstraints());

        // Add to frame
        add(backgroundPanel);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(UserDashboard::new);
    }
}