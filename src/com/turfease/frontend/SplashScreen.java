package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JFrame {

    public SplashScreen() {
        // Frame setup
        setTitle("TurfEase - Welcome");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setUndecorated(true); // no title bar

        // Background panel
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon bg = new ImageIcon("resources/football1.jpg"); // ⚽ replace with your football image
                g.drawImage(bg.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(new BorderLayout());

        // Title text
        JLabel title = new JLabel("TurfEase", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 40));
        title.setForeground(Color.WHITE);
        panel.add(title, BorderLayout.CENTER);

        // Footer text
        JLabel footer = new JLabel("Welcome to Turf Booking System", SwingConstants.CENTER);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        footer.setForeground(Color.LIGHT_GRAY);
        panel.add(footer, BorderLayout.SOUTH);

        add(panel);

        // Timer: show splash for 3 seconds, then go to HomePage
        Timer timer = new Timer(3000, e -> {
            dispose(); // close splash
            new HomePage(); // open homepage
        });
        timer.setRepeats(false);
        timer.start();

        setVisible(true);
    }

    public static void main(String[] args) {
        new SplashScreen();
    }
}
