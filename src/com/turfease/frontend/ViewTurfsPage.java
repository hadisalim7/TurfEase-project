package com.turfease.frontend;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewTurfsPage {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Available Turfs - TurfEase");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        String[] columnNames = {"Turf ID", "Turf Name", "Location", "Price per Hour"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton viewSlots = new JButton("View Slots");
        frame.add(viewSlots, BorderLayout.SOUTH);

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
            JOptionPane.showMessageDialog(frame, "Database Error: " + e.getMessage());
        }

        viewSlots.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "Please select a turf first!");
                return;
            }
            int turfId = (int) model.getValueAt(row, 0);
            String turfName = model.getValueAt(row, 1).toString();
            frame.dispose();
            SlotsPage.main(new String[]{String.valueOf(turfId), turfName});
        });

        frame.setVisible(true);
    }
}
