package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ViewTurfsPage extends JFrame {
    public ViewTurfsPage() {
        setTitle("Available Turfs - TurfEase");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 🌟 Header
        JPanel header = new JPanel();
        header.setBackground(new Color(34, 139, 34));
        JLabel title = new JLabel("Available Turfs");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // 🌟 Scrollable turf cards
        JPanel turfPanel = new JPanel(new GridLayout(0, 2, 15, 15)); // 2 columns
        turfPanel.setBackground(new Color(245, 245, 245));
        JScrollPane scrollPane = new JScrollPane(turfPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(scrollPane, BorderLayout.CENTER);

        String url = "jdbc:mysql://localhost:3306/turfease_db";
        String username = "root";
        String password = "hadi123";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT turf_id, turf_name, location, price_per_hour, photo_url FROM turfs")) {

            while (rs.next()) {
                int turfId = rs.getInt("turf_id");
                String turfName = rs.getString("turf_name");
                String location = rs.getString("location");
                double price = rs.getDouble("price_per_hour");
                String photo = rs.getString("photo_url");

                // 🌟 Card panel
                JPanel card = new JPanel(new BorderLayout());
                card.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
                card.setBackground(Color.WHITE);

                // Image
                JLabel imageLabel;
                if (photo != null && !photo.isEmpty()) {
                    ImageIcon img = new ImageIcon(photo);
                    Image scaled = img.getImage().getScaledInstance(350, 150, Image.SCALE_SMOOTH);
                    imageLabel = new JLabel(new ImageIcon(scaled));
                } else {
                    imageLabel = new JLabel("No Image", SwingConstants.CENTER);
                    imageLabel.setPreferredSize(new Dimension(350, 150));
                }
                card.add(imageLabel, BorderLayout.NORTH);

                // Details
                JPanel details = new JPanel();
                details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
                details.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                details.setBackground(Color.WHITE);

                JLabel nameLabel = new JLabel(turfName);
                nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
                JLabel locationLabel = new JLabel("📍 " + location);
                JLabel priceLabel = new JLabel("₹" + price + " / hr");

                details.add(nameLabel);
                details.add(locationLabel);
                details.add(priceLabel);

                card.add(details, BorderLayout.CENTER);

                // Button
                JButton bookButton = new JButton("View Slots");
                bookButton.setBackground(new Color(34, 139, 34));
                bookButton.setForeground(Color.WHITE);
                bookButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
                bookButton.addActionListener(e -> {
                    dispose();
                    new SlotsPage(turfId, turfName);
                });

                card.add(bookButton, BorderLayout.SOUTH);

                turfPanel.add(card);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }

        // 🔙 Back on minimize → UserDashboard
        addWindowStateListener(e -> {
            if ((e.getNewState() & Frame.ICONIFIED) == Frame.ICONIFIED) {
                dispose();
                new UserDashboard();
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ViewTurfsPage::new);
    }
}
