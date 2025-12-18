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
        setBackground(new Color(240, 240, 245));

        // Card Panel
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(40, 50, 40, 50)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Job Sphere");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(30, 30, 30));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        card.add(titleLabel, gbc);

        // Subtitle
        JLabel subLabel = new JLabel("Login to your account");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subLabel.setForeground(new Color(100, 100, 100));
        subLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 5, 25, 5);
        card.add(subLabel, gbc);

        gbc.insets = new Insets(8, 5, 8, 5);

        // Username
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.BLACK);
        card.add(userLabel, gbc);

        userField = new JTextField(20);
        gbc.gridx = 1;
        card.add(userField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.BLACK);
        card.add(passLabel, gbc);

        passField = new JPasswordField(20);
        gbc.gridx = 1;
        card.add(passField, gbc);

        // Role
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setForeground(Color.BLACK);
        card.add(roleLabel, gbc);

        roleCombo = new JComboBox<>(new String[] { "APPLICANT", "COMPANY" });
        gbc.gridx = 1;
        card.add(roleCombo, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setOpaque(false);

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        styleButton(loginBtn, new Color(59, 130, 246), Color.WHITE);
        styleButton(registerBtn, new Color(180, 180, 180), Color.BLACK);

        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 5, 5, 5);
        card.add(btnPanel, gbc);

        loginBtn.addActionListener(e -> handleLogin());
        registerBtn.addActionListener(e -> handleRegister());

        add(card);
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void handleLogin() {
        String user = userField.getText();
        String pass = new String(passField.getPassword());

        try {
            User loggedInUser = DataManager.getInstance().login(user, pass);
            if (loggedInUser != null) {
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
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(this,
                    "Account blocked!\nToo many failed login attempts.\nPlease try again later.",
                    "Account Blocked",
                    JOptionPane.WARNING_MESSAGE);
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
