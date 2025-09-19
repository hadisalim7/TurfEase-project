package com.turfease.frontend;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewTurfsPage extends JFrame {

    public ViewTurfsPage() {
        setTitle("Available Turfs - TurfEase");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(0, 10));

        // ===== Header =====
        JPanel header = new JPanel();
        header.setBackground(new Color(34, 153, 84));
        JLabel title = new JLabel("Available Turfs");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // ===== Table Panel =====
        String[] columnNames = {"Turf ID", "Turf Name", "Location", "Price per Hour"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.setRowHeight(28);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // ===== Button Panel =====
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(236, 240, 241));
        JButton viewSlots = new JButton("View Slots");
        viewSlots.setFont(new Font("Segoe UI", Font.BOLD, 16));
        viewSlots.setBackground(new Color(46, 204, 113));
        viewSlots.setForeground(Color.WHITE);
        viewSlots.setFocusPainted(false);
        buttonPanel.add(viewSlots);
        add(buttonPanel, BorderLayout.SOUTH);

        // ===== Load Turfs from DB =====
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/turfease_db", "root", "hadi123");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT turf_id, turf_name, location, price_per_hour FROM turfs")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("turf_id"),
                        rs.getString("turf_name"),
                        rs.getString("location"),
                        rs.getDouble("price_per_hour")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }

        // ===== Button Action =====
        viewSlots.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a turf first!");
                return;
            }
            int turfId = (int) model.getValueAt(row, 0);
            String turfName = model.getValueAt(row, 1).toString();
            dispose();
            new SlotsPage(turfId, turfName);
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ViewTurfsPage::new);
    }
}
