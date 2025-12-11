package com.jobsphere.ui;

import com.jobsphere.core.*;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    private MainFrame mainFrame;
    private JTextField userField;
    private JPasswordField passField;
    private JComboBox<String> roleCombo;

    public LoginPanel(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new GridBagLayout());
        setBackground(new Color(240, 248, 255)); // Alice Blue

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Job Sphere Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        add(new JLabel("Username:"), gbc);
        userField = new JTextField(15);
        gbc.gridx = 1;
        add(userField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Password:"), gbc);
        passField = new JPasswordField(15);
        gbc.gridx = 1;
        add(passField, gbc);

        // Role (for Registration)
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Role (Register only):"), gbc);
        roleCombo = new JComboBox<>(new String[] { "APPLICANT", "COMPANY" });
        gbc.gridx = 1;
        add(roleCombo, gbc);

        // Buttons
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        // Styling Buttons
        loginBtn.setBackground(new Color(100, 149, 237));
        loginBtn.setForeground(Color.WHITE);
        registerBtn.setBackground(new Color(60, 179, 113));
        registerBtn.setForeground(Color.WHITE);

        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(btnPanel, gbc);

        // Actions
        loginBtn.addActionListener(e -> handleLogin());
        registerBtn.addActionListener(e -> handleRegister());
    }

    private void handleLogin() {
        String user = userField.getText();
        String pass = new String(passField.getPassword());

        User loggedInUser = DataManager.getInstance().login(user, pass);
        if (loggedInUser != null) {
            JOptionPane.showMessageDialog(this, "Welcome " + loggedInUser.getUsername());
            if (loggedInUser instanceof Applicant) {
                mainFrame.addCard(new ApplicantPanel(mainFrame), "APPLICANT");
                mainFrame.showCard("APPLICANT");
            } else {
                mainFrame.addCard(new CompanyPanel(mainFrame), "COMPANY");
                mainFrame.showCard("COMPANY");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRegister() {
        String user = userField.getText();
        String pass = new String(passField.getPassword());
        String role = (String) roleCombo.getSelectedItem();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields");
            return;
        }

        try {
            User newUser = UserFactory.createUser(role, user, pass, user + "@example.com");
            DataManager.getInstance().registerUser(newUser);
            JOptionPane.showMessageDialog(this, "Registration Successful! Please Login.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
