package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HomePage {

    public static void main(String[] args) {
        JFrame frame = new JFrame("TurfEase - Home");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel title = new JLabel("Welcome to TurfEase", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        frame.add(title, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1, 10, 10));

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        // When Login is clicked → open LoginPage
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // close current page
                LoginPage.main(null); // open login page
            }
        });

        // When Register is clicked → open RegisterPage
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // close current page
                RegisterPage.main(null); // open register page
            }
        });

        panel.add(loginButton);
        panel.add(registerButton);

        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}
