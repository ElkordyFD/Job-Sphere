package com.jobsphere.ui;

import com.jobsphere.core.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CompanyPanel extends JPanel {
    private MainFrame mainFrame;
    private JTable appsTable;
    private DefaultTableModel appsModel;
    private JTable myJobsTable;
    private DefaultTableModel jobsModel;

    public CompanyPanel(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(new Color(70, 130, 180));
        JLabel title = new JLabel("Company Dashboard");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            DataManager.getInstance().logout();
            mainFrame.showCard("LOGIN");
        });

        header.add(title);
        header.add(Box.createHorizontalStrut(400));
        header.add(logoutBtn);
        add(header, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();

        // Tab 1: Post Job
        JPanel postJobPanel = createPostJobPanel();
        tabs.addTab("Post New Job", postJobPanel);

        // Tab 2: Manage Applications
        JPanel manageAppsPanel = createManageAppsPanel();
        tabs.addTab("Manage Applications", manageAppsPanel);

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createPostJobPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField titleField = new JTextField(20);
        JTextArea descArea = new JTextArea(5, 20);
        JTextArea reqArea = new JTextArea(3, 20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Job Title:"), gbc, panel);
        gbc.gridx = 1;
        add(titleField, gbc, panel);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Description:"), gbc, panel);
        gbc.gridx = 1;
        add(new JScrollPane(descArea), gbc, panel);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Requirements:"), gbc, panel);
        gbc.gridx = 1;
        add(new JScrollPane(reqArea), gbc, panel);

        JButton postBtn = new JButton("Post Job");
        postBtn.setBackground(new Color(60, 179, 113));
        postBtn.setForeground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(postBtn, gbc, panel);

        postBtn.addActionListener(e -> {
            User user = DataManager.getInstance().getCurrentUser();
            Job job = new JobBuilder()
                    .setTitle(titleField.getText())
                    .setDescription(descArea.getText())
                    .setRequirements(reqArea.getText())
                    .setCompanyUsername(user.getUsername())
                    .build();
            DataManager.getInstance().addJob(job);
            JOptionPane.showMessageDialog(this, "Job Posted!");
            titleField.setText("");
            descArea.setText("");
            reqArea.setText("");
            refreshApps(); // Refresh tables if needed
        });

        return panel;
    }

    private void add(Component comp, GridBagConstraints gbc, JPanel panel) {
        panel.add(comp, gbc);
    }

    private JPanel createManageAppsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JButton refreshBtn = new JButton("Refresh Applications");
        refreshBtn.addActionListener(e -> refreshApps());
        panel.add(refreshBtn, BorderLayout.NORTH);

        String[] cols = { "Job Title", "Applicant", "Status" };
        appsModel = new DefaultTableModel(cols, 0);
        appsTable = new JTable(appsModel);
        panel.add(new JScrollPane(appsTable), BorderLayout.CENTER);

        JButton nextStateBtn = new JButton("Move to Next Stage");
        nextStateBtn.addActionListener(e -> moveState());
        panel.add(nextStateBtn, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshApps() {
        appsModel.setRowCount(0);
        User user = DataManager.getInstance().getCurrentUser();
        // Get all jobs for this company
        List<Job> myJobs = DataManager.getInstance().getJobsByCompany(user.getUsername());

        for (Job job : myJobs) {
            List<JobApplication> apps = DataManager.getInstance().getApplicationsForJob(job.getId());
            for (JobApplication app : apps) {
                appsModel.addRow(new Object[] { job.getTitle(), app.getApplicantUsername(), app.getStatus() });
            }
        }
    }

    private void moveState() {
        int row = appsTable.getSelectedRow();
        if (row == -1)
            return;

        // This is a bit tricky since we need the actual object.
        // For simplicity, we'll search again or store it in the model (not shown for
        // brevity)
        // Let's just re-fetch based on row index is risky if sorted, but okay for
        // simple demo
        // Better: Store Application object in a hidden column or separate list

        // Re-fetching logic for demo:
        String jobTitle = (String) appsModel.getValueAt(row, 0);
        String applicant = (String) appsModel.getValueAt(row, 1);

        // Find the application
        // In a real app, use IDs. Here we iterate.
        List<JobApplication> allApps = DataManager.getInstance().getApplicationsByUser(applicant);
        for (JobApplication app : allApps) {
            if (app.getJob().getTitle().equals(jobTitle)) {
                app.next(); // State Pattern in action
                break;
            }
        }
        refreshApps();
    }
}
