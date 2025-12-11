package com.jobsphere.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainFrame() {
        setTitle("Job Sphere");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add Panels
        mainPanel.add(new LoginPanel(this), "LOGIN");
        // Dashboards will be added dynamically or pre-added if stateless enough
        // For simplicity, we might re-instantiate or just add them now

        add(mainPanel);
    }

    public void showCard(String cardName) {
        cardLayout.show(mainPanel, cardName);
    }

    public void addCard(JPanel panel, String name) {
        mainPanel.add(panel, name);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
