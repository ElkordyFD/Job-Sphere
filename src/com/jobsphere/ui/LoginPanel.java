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
        setLayout(new GridBagLayout()); // Center the card
        setBackground(new Color(225, 240, 255)); // Soft Background

        // Card Panel
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40) // Padding inside card
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Job Sphere");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(50, 50, 50));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        card.add(titleLabel, gbc);

        // Subtitle
        JLabel subLabel = new JLabel("Login to your account");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subLabel.setForeground(Color.GRAY);
        subLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 5, 20, 5);
        card.add(subLabel, gbc);

        // Reset insets
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        card.add(new JLabel("Username:"), gbc);

        userField = new JTextField(20);
        gbc.gridx = 1;
        card.add(userField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        card.add(new JLabel("Password:"), gbc);

        passField = new JPasswordField(20);
        gbc.gridx = 1;
        card.add(passField, gbc);

        // Role
        gbc.gridx = 0;
        gbc.gridy = 4;
        card.add(new JLabel("Role:"), gbc);

        roleCombo = new JComboBox<>(new String[] { "APPLICANT", "COMPANY" });
        gbc.gridx = 1;
        card.add(roleCombo, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setOpaque(false);

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        styleButton(loginBtn, new Color(70, 130, 180));
        styleButton(registerBtn, new Color(100, 100, 100));

        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        card.add(btnPanel, gbc);

        // Actions
        loginBtn.addActionListener(e -> handleLogin());
        registerBtn.addActionListener(e -> handleRegister());

        // Add Card to Main Panel
        add(card);
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
    }

    private void handleLogin() {
        String user = userField.getText();
        String pass = new String(passField.getPassword());

        User loggedInUser = DataManager.getInstance().login(user, pass);
        if (loggedInUser != null) {
            // Keep it silent or simple welcome
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
