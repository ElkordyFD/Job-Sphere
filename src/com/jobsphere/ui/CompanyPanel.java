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
    private JTable candidatesTable;
    private DefaultTableModel candidatesModel;
    private JTextField candidateSearchField;

    public CompanyPanel(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 250));

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        header.setBackground(new Color(124, 58, 237));

        JLabel title = new JLabel("Company Dashboard");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JButton logoutBtn = new JButton("Logout");
        styleHeaderButton(logoutBtn);
        logoutBtn.addActionListener(e -> {
            DataManager.getInstance().logout();
            mainFrame.showCard("LOGIN");
        });

        header.add(title);
        header.add(Box.createHorizontalStrut(350));
        header.add(logoutBtn);
        add(header, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabs.addTab("Post New Job", createPostJobPanel());
        tabs.addTab("Manage Applications", createManageAppsPanel());
        tabs.addTab("Manage Jobs", createManageJobsPanel());
        tabs.addTab("Search Candidates", createSearchCandidatesPanel());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createPostJobPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField titleField = new JTextField(25);
        JTextArea descArea = new JTextArea(5, 25);
        JTextArea reqArea = new JTextArea(3, 25);

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel titleLabel = new JLabel("Job Title:");
        titleLabel.setForeground(Color.BLACK);
        panel.add(titleLabel, gbc);

        gbc.gridx = 1;
        panel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel descLabel = new JLabel("Description:");
        descLabel.setForeground(Color.BLACK);
        panel.add(descLabel, gbc);

        gbc.gridx = 1;
        panel.add(new JScrollPane(descArea), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel reqLabel = new JLabel("Requirements:");
        reqLabel.setForeground(Color.BLACK);
        panel.add(reqLabel, gbc);

        gbc.gridx = 1;
        panel.add(new JScrollPane(reqArea), gbc);

        JButton postBtn = new JButton("Post Job");
        styleButton(postBtn, new Color(34, 197, 94), Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(postBtn, gbc);

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
            refreshMyJobs();
            refreshApps();
        });

        return panel;
    }

    private JPanel createManageAppsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 250));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setBackground(Color.WHITE);
        JButton refreshBtn = new JButton("Refresh Applications");
        styleButton(refreshBtn, new Color(59, 130, 246), Color.WHITE);
        refreshBtn.addActionListener(e -> refreshApps());
        topPanel.add(refreshBtn);
        panel.add(topPanel, BorderLayout.NORTH);

        String[] cols = { "Job Title", "Applicant", "Status" };
        appsModel = new DefaultTableModel(cols, 0);
        appsTable = new JTable(appsModel);
        styleTable(appsTable);
        panel.add(new JScrollPane(appsTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        btnPanel.setBackground(Color.WHITE);

        JButton nextStateBtn = new JButton("Move to Next Stage");
        styleButton(nextStateBtn, new Color(34, 197, 94), Color.WHITE);
        nextStateBtn.addActionListener(e -> moveState());

        JButton viewResumeBtn = new JButton("View Resume");
        styleButton(viewResumeBtn, new Color(59, 130, 246), Color.WHITE);
        viewResumeBtn.addActionListener(e -> viewResume());

        JButton viewProfileBtn = new JButton("View Profile");
        styleButton(viewProfileBtn, new Color(180, 180, 180), Color.BLACK);
        viewProfileBtn.addActionListener(e -> viewApplicantProfile());

        btnPanel.add(nextStateBtn);
        btnPanel.add(viewResumeBtn);
        btnPanel.add(viewProfileBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createManageJobsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 250));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setBackground(Color.WHITE);
        JButton refreshBtn = new JButton("Refresh Jobs");
        styleButton(refreshBtn, new Color(59, 130, 246), Color.WHITE);
        refreshBtn.addActionListener(e -> refreshMyJobs());
        topPanel.add(refreshBtn);
        panel.add(topPanel, BorderLayout.NORTH);

        String[] cols = { "Title", "Status" };
        jobsModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        myJobsTable = new JTable(jobsModel);
        styleTable(myJobsTable);
        panel.add(new JScrollPane(myJobsTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        btnPanel.setBackground(Color.WHITE);

        JButton editBtn = new JButton("Edit Title");
        styleButton(editBtn, new Color(59, 130, 246), Color.WHITE);
        editBtn.addActionListener(e -> editJob());

        JButton toggleBtn = new JButton("Pause/Resume");
        styleButton(toggleBtn, new Color(245, 158, 11), Color.BLACK);
        toggleBtn.addActionListener(e -> toggleJobStatus());

        JButton removeBtn = new JButton("Remove");
        styleButton(removeBtn, new Color(239, 68, 68), Color.WHITE);
        removeBtn.addActionListener(e -> removeJob());

        btnPanel.add(editBtn);
        btnPanel.add(toggleBtn);
        btnPanel.add(removeBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSearchCandidatesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 250));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        searchPanel.setBackground(Color.WHITE);

        JLabel searchLabel = new JLabel("Name/Email:");
        searchLabel.setForeground(Color.BLACK);

        candidateSearchField = new JTextField(20);

        JButton searchBtn = new JButton("Search");
        styleButton(searchBtn, new Color(59, 130, 246), Color.WHITE);
        searchBtn.addActionListener(e -> searchCandidates());

        searchPanel.add(searchLabel);
        searchPanel.add(candidateSearchField);
        searchPanel.add(searchBtn);
        panel.add(searchPanel, BorderLayout.NORTH);

        String[] cols = { "Username", "Email" };
        candidatesModel = new DefaultTableModel(cols, 0);
        candidatesTable = new JTable(candidatesModel);
        styleTable(candidatesTable);
        panel.add(new JScrollPane(candidatesTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        btnPanel.setBackground(Color.WHITE);
        JButton viewProfileBtn = new JButton("View Candidate Profile");
        styleButton(viewProfileBtn, new Color(59, 130, 246), Color.WHITE);
        viewProfileBtn.addActionListener(e -> viewCandidateProfile());
        btnPanel.add(viewProfileBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleHeaderButton(JButton btn) {
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(124, 58, 237));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(Color.BLACK);
        table.setSelectionBackground(new Color(124, 58, 237));
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.getTableHeader().setForeground(Color.BLACK);
    }

    private void refreshApps() {
        appsModel.setRowCount(0);
        User user = DataManager.getInstance().getCurrentUser();
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

        String jobTitle = (String) appsModel.getValueAt(row, 0);
        String applicant = (String) appsModel.getValueAt(row, 1);

        List<JobApplication> allApps = DataManager.getInstance().getApplicationsByUser(applicant);
        for (JobApplication app : allApps) {
            if (app.getJob().getTitle().equals(jobTitle)) {
                app.next();
                break;
            }
        }
        refreshApps();
    }

    private void viewResume() {
        int row = appsTable.getSelectedRow();
        if (row == -1)
            return;

        String jobTitle = (String) appsModel.getValueAt(row, 0);
        String applicantName = (String) appsModel.getValueAt(row, 1);

        JobApplication app = findApplication(jobTitle, applicantName);
        if (app != null && app.getResumePath() != null) {
            try {
                Desktop.getDesktop().open(new java.io.File(app.getResumePath()));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error opening resume: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "No resume found.");
        }
    }

    private void viewApplicantProfile() {
        int row = appsTable.getSelectedRow();
        if (row == -1)
            return;
        String applicantName = (String) appsModel.getValueAt(row, 1);
        showUserProfile(applicantName);
    }

    private JobApplication findApplication(String jobTitle, String applicantName) {
        List<JobApplication> allApps = DataManager.getInstance().getApplicationsByUser(applicantName);
        for (JobApplication app : allApps) {
            if (app.getJob().getTitle().equals(jobTitle)) {
                return app;
            }
        }
        return null;
    }

    private void refreshMyJobs() {
        jobsModel.setRowCount(0);
        User user = DataManager.getInstance().getCurrentUser();
        List<Job> myJobs = DataManager.getInstance().getJobsByCompany(user.getUsername());
        for (Job j : myJobs) {
            jobsModel.addRow(new Object[] { j.getTitle(), j.isActive() ? "Active" : "Paused" });
        }
    }

    private void editJob() {
        int row = myJobsTable.getSelectedRow();
        if (row == -1)
            return;
        String jobTitle = (String) jobsModel.getValueAt(row, 0);
        Job job = findJobByTitle(jobTitle);
        if (job != null) {
            String newTitle = JOptionPane.showInputDialog(this, "Enter new title:", job.getTitle());
            if (newTitle != null && !newTitle.isEmpty()) {
                job.setTitle(newTitle);
                refreshMyJobs();
            }
        }
    }

    private void toggleJobStatus() {
        int row = myJobsTable.getSelectedRow();
        if (row == -1)
            return;
        String jobTitle = (String) jobsModel.getValueAt(row, 0);
        Job job = findJobByTitle(jobTitle);
        if (job != null) {
            job.setActive(!job.isActive());
            refreshMyJobs();
        }
    }

    private void removeJob() {
        int row = myJobsTable.getSelectedRow();
        if (row == -1)
            return;
        String jobTitle = (String) jobsModel.getValueAt(row, 0);
        Job job = findJobByTitle(jobTitle);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?", "Remove Job", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION && job != null) {
            DataManager.getInstance().removeJob(job.getId());
            refreshMyJobs();
        }
    }

    private Job findJobByTitle(String title) {
        User user = DataManager.getInstance().getCurrentUser();
        return DataManager.getInstance().getJobsByCompany(user.getUsername()).stream()
                .filter(j -> j.getTitle().equals(title))
                .findFirst().orElse(null);
    }

    private void searchCandidates() {
        candidatesModel.setRowCount(0);
        String query = candidateSearchField.getText().toLowerCase();
        List<User> applicants = DataManager.getInstance().getAllApplicants();
        for (User u : applicants) {
            if (u.getUsername().toLowerCase().contains(query)
                    || (u.getEmail() != null && u.getEmail().toLowerCase().contains(query))) {
                candidatesModel.addRow(new Object[] { u.getUsername(), u.getEmail() });
            }
        }
    }

    private void viewCandidateProfile() {
        int row = candidatesTable.getSelectedRow();
        if (row == -1)
            return;
        String username = (String) candidatesModel.getValueAt(row, 0);
        showUserProfile(username);
    }

    private void showUserProfile(String username) {
        List<User> applicants = DataManager.getInstance().getAllApplicants();
        User target = applicants.stream().filter(u -> u.getUsername().equals(username)).findFirst().orElse(null);

        if (target != null) {
            Applicant app = (Applicant) target;
            String info = "Username: " + app.getUsername() + "\n" +
                    "Email: " + app.getEmail() + "\n" +
                    "Resume Path: " + app.getResumePath();
            JOptionPane.showMessageDialog(this, info, "Candidate Profile", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
