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

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(new Color(70, 130, 180));
        JLabel title = new JLabel("Company Dashboard");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            DataManager.getInstance().getAuthService().logout();
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

        // Tab 3: Manage Jobs
        JPanel manageJobsPanel = createManageJobsPanel();
        tabs.addTab("Manage Jobs", manageJobsPanel);

        // Tab 4: Search Candidates
        JPanel searchCandidatesPanel = createSearchCandidatesPanel();
        tabs.addTab("Search Candidates", searchCandidatesPanel);

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
            User user = DataManager.getInstance().getAuthService().getCurrentUser();
            Job job = new JobBuilder()
                    .setTitle(titleField.getText())
                    .setDescription(descArea.getText())
                    .setRequirements(reqArea.getText())
                    .setCompanyUsername(user.getUsername())
                    .build();
            DataManager.getInstance().getJobRepository().addJob(job);
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

        JButton viewResumeBtn = new JButton("View Resume");
        viewResumeBtn.addActionListener(e -> viewResume());

        JButton viewProfileBtn = new JButton("View Profile");
        viewProfileBtn.addActionListener(e -> viewApplicantProfile());

        JPanel btnPanel = new JPanel();
        btnPanel.add(nextStateBtn);
        btnPanel.add(viewResumeBtn);
        btnPanel.add(viewProfileBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createManageJobsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JButton refreshBtn = new JButton("Refresh Jobs");
        refreshBtn.addActionListener(e -> refreshMyJobs());
        panel.add(refreshBtn, BorderLayout.NORTH);

        String[] cols = { "ID", "Title", "Status" };
        jobsModel = new DefaultTableModel(cols, 0);
        myJobsTable = new JTable(jobsModel);
        panel.add(new JScrollPane(myJobsTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton editBtn = new JButton("Edit Title");
        editBtn.addActionListener(e -> editJob());

        JButton toggleBtn = new JButton("Pause/Resume");
        toggleBtn.addActionListener(e -> toggleJobStatus());

        JButton removeBtn = new JButton("Remove");
        removeBtn.addActionListener(e -> removeJob());

        btnPanel.add(editBtn);
        btnPanel.add(toggleBtn);
        btnPanel.add(removeBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSearchCandidatesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel searchPanel = new JPanel();
        candidateSearchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> searchCandidates());
        searchPanel.add(new JLabel("Name/Email:"));
        searchPanel.add(candidateSearchField);
        searchPanel.add(searchBtn);
        panel.add(searchPanel, BorderLayout.NORTH);

        String[] cols = { "Username", "Email" };
        candidatesModel = new DefaultTableModel(cols, 0);
        candidatesTable = new JTable(candidatesModel);
        panel.add(new JScrollPane(candidatesTable), BorderLayout.CENTER);

        JButton viewProfileBtn = new JButton("View Candidate Profile");
        viewProfileBtn.addActionListener(e -> viewCandidateProfile());
        panel.add(viewProfileBtn, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshApps() {
        appsModel.setRowCount(0);
        User user = DataManager.getInstance().getAuthService().getCurrentUser();
        // Get all jobs for this company
        List<Job> myJobs = DataManager.getInstance().getJobRepository().getJobsByCompany(user.getUsername());

        for (Job job : myJobs) {
            List<JobApplication> apps = DataManager.getInstance().getApplicationRepository().getApplicationsForJob(job.getId());
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

        List<JobApplication> allApps = DataManager.getInstance().getApplicationRepository().getApplicationsByUser(applicant);
        for (JobApplication app : allApps) {
            if (app.getJob().getTitle().equals(jobTitle)) {
                app.next(); // State Pattern in action
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
        List<JobApplication> allApps = DataManager.getInstance().getApplicationRepository().getApplicationsByUser(applicantName);
        for (JobApplication app : allApps) {
            if (app.getJob().getTitle().equals(jobTitle)) {
                return app;
            }
        }
        return null;
    }

    // Job Management Methods
    private void refreshMyJobs() {
        jobsModel.setRowCount(0);
        User user = DataManager.getInstance().getAuthService().getCurrentUser();
        List<Job> myJobs = DataManager.getInstance().getJobRepository().getJobsByCompany(user.getUsername());
        for (Job j : myJobs) {
            jobsModel.addRow(new Object[] { j.getId(), j.getTitle(), j.isActive() ? "Active" : "Paused" });
        }
    }

    private void editJob() {
        int row = myJobsTable.getSelectedRow();
        if (row == -1)
            return;
        String jobId = (String) jobsModel.getValueAt(row, 0);
        Job job = findJob(jobId);
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
        String jobId = (String) jobsModel.getValueAt(row, 0);
        Job job = findJob(jobId);
        if (job != null) {
            job.setActive(!job.isActive());
            refreshMyJobs();
        }
    }

    private void removeJob() {
        int row = myJobsTable.getSelectedRow();
        if (row == -1)
            return;
        String jobId = (String) jobsModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?", "Remove Job", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            DataManager.getInstance().getJobRepository().removeJob(jobId);
            refreshMyJobs();
        }
    }

    private Job findJob(String jobId) {
        return DataManager.getInstance().getJobRepository().getJobs().stream()
                .filter(j -> j.getId().equals(jobId))
                .findFirst().orElse(null);
    }

    // Candidate Search Methods
    private void searchCandidates() {
        candidatesModel.setRowCount(0);
        String query = candidateSearchField.getText().toLowerCase();
        List<User> applicants = DataManager.getInstance().getUserRepository().getAllApplicants();
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
        // In a real app, we'd fetch the user object.
        // For now, we iterate.
        List<User> applicants = DataManager.getInstance().getUserRepository().getAllApplicants();
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
