package com.turfease.frontend;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewTurfsPage extends JFrame {
    public ViewTurfsPage() {
        setTitle("Available Turfs - TurfEase");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        String[] columnNames = {"Turf ID", "Turf Name", "Location", "Price per Hour"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton viewSlots = new JButton("View Slots");
        add(viewSlots, BorderLayout.SOUTH);

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
