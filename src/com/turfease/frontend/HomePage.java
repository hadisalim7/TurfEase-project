package com.turfease.frontend;


import javax.swing.*;  
import java.awt.*;     
import java.awt.event.ActionEvent; 

public class HomePage extends JFrame { 
    public HomePage() { 
        setTitle("TurfEase - Home");

    
        setSize(420, 300);

        
        setLocationRelativeTo(null);

        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    
        JLabel title = new JLabel("Welcome to TurfEase", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

    
        loginButton.addActionListener((ActionEvent e) -> {
        
            JOptionPane.showMessageDialog(this, "Login button clicked!");
        });

        registerButton.addActionListener((ActionEvent e) -> {
            JOptionPane.showMessageDialog(this, "Register button clicked!");
        });

    
        JPanel root = new JPanel(new GridLayout(3, 1, 10, 10));

        
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    
        root.add(title);
        root.add(loginButton);
        root.add(registerButton);

        
        setContentPane(root);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HomePage().setVisible(true));
    }
}