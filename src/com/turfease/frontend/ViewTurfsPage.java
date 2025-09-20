package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ViewTurfsPage extends JFrame {
    public ViewTurfsPage() {
        setTitle("Available Turfs - TurfEase");
        setSize(650, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); // vertical stacking

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        add(scrollPane);

        String url = "jdbc:mysql://localhost:3306/turfease_db";
        String username = "root";
        String password = "hadi123";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT turf_id, turf_name, location, price_per_hour, photo_url FROM turfs")) {

            while (rs.next()) {
                int turfId = rs.getInt("turf_id");
                String name = rs.getString("turf_name");
                String location = rs.getString("location");
                double price = rs.getDouble("price_per_hour");
                String photoPath = rs.getString("photo_url");

                JPanel card = new JPanel(new BorderLayout());
                card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                card.setBackground(Color.WHITE);
                card.setMaximumSize(new Dimension(600, 180)); // smaller height

                // Turf Image
                JLabel imageLabel;
                if (photoPath != null && !photoPath.isEmpty()) {
                    ImageIcon img = new ImageIcon(photoPath);
                    Image scaled = img.getImage().getScaledInstance(120, 100, Image.SCALE_SMOOTH);
                    imageLabel = new JLabel(new ImageIcon(scaled));
                } else {
                    imageLabel = new JLabel("No Image", SwingConstants.CENTER);
                }
                card.add(imageLabel, BorderLayout.WEST);

                // Turf details
                JPanel detailsPanel = new JPanel();
                detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
                JLabel nameLabel = new JLabel(name);
                nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
                JLabel locationLabel = new JLabel("📍 " + location);
                JLabel priceLabel = new JLabel("₹" + price + " / hour");

                detailsPanel.add(nameLabel);
                detailsPanel.add(locationLabel);
                detailsPanel.add(priceLabel);

                card.add(detailsPanel, BorderLayout.CENTER);

                // View Slots button
                JButton viewSlots = new JButton("View Slots");
                viewSlots.setBackground(new Color(34, 139, 34));
                viewSlots.setForeground(Color.WHITE);
                viewSlots.addActionListener(e -> {
                    dispose();
                    new SlotsPage(turfId, name);
                });
                card.add(viewSlots, BorderLayout.EAST);

                mainPanel.add(card);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }

        // Minimize → back to dashboard
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
