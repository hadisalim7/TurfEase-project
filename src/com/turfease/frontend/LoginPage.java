package com.turfease.frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginPage extends JFrame{
    public LoginPage(){
        setTitle("Turfease-Login");
        setSize(400,500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();
        JLabel passwordLabel = new JLabel("Password");
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("login");
        loginButton.addActionListener((ActionEvent e)->{
             String email=emailField.getText();
             String password=new String(passwordField.getPassword());
             if(email.isEmpty()||password.isEmpty()){
                JOptionPane.showMessageDialog(this,"Please fill all fields!");

            }
            else{
                JOptionPane.showMessageDialog(this,"Logi Succesfull for:"+email);
                dispose();

            }
        });
        JPanel panel = new JPanel(new GridLayout(3,2,10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel());
        panel.add(loginButton);
        setContentPane(panel);
    }

public static void main(String[] args){
    SwingUtilities.invokeLater(()-> new LoginPage().setVisible(true));

}    
}