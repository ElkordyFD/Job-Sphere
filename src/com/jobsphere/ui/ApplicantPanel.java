package com.jobsphere.ui;

import com.jobsphere.core.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ApplicantPanel extends JPanel {
    private MainFrame mainFrame;
    private JTable jobTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private SearchStrategy searchStrategy;
    private JToggleButton showSavedBtn;
    private JButton saveBtn;
    private static final String RESUME_DIR = "resumes";
    private JTable myAppsTable;
    private DefaultTableModel myAppsModel;

    public ApplicantPanel(MainFrame frame) {
        this.mainFrame = frame;
        this.searchStrategy = new KeywordSearchStrategy();
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 250));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        header.setBackground(new Color(59, 130, 246));

        JLabel title = new JLabel("Applicant Dashboard");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JButton profileBtn = new JButton("Profile");
        profileBtn.setBackground(Color.WHITE);
        profileBtn.setForeground(new Color(59, 130, 246));
        profileBtn.addActionListener(e -> showProfile());

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(Color.WHITE);
        logoutBtn.setForeground(new Color(59, 130, 246));
        logoutBtn.addActionListener(e -> {
            DataManager.getInstance().logout();
            mainFrame.showCard("LOGIN");
        });

        header.add(title);
        header.add(Box.createHorizontalStrut(300));
        header.add(profileBtn);
        header.add(logoutBtn);
        add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.addTab("Browse Jobs", createBrowseJobsPanel());
        tabs.addTab("My Applications", createMyApplicationsPanel());
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 1) {
                refreshMyApplications();
            }
        });
        add(tabs, BorderLayout.CENTER);
        refreshJobList();
    }

    private JPanel createBrowseJobsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 250));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.add(new JLabel("Keywords:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);

        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(59, 130, 246));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.addActionListener(e -> refreshJobList());
        searchPanel.add(searchBtn);

        showSavedBtn = new JToggleButton("Show Saved Only");
        showSavedBtn.setBackground(new Color(180, 180, 180));
        showSavedBtn.setForeground(Color.BLACK);
        showSavedBtn.addActionListener(e -> refreshJobList());
        searchPanel.add(showSavedBtn);
        panel.add(searchPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Title", "Company", "Description", "Requirements"};
        tableModel = new DefaultTableModel(cols, 0);
        jobTable = new JTable(tableModel);
        jobTable.setRowHeight(35);
        jobTable.setSelectionBackground(new Color(59, 130, 246));
        jobTable.getSelectionModel().addListSelectionListener(e -> updateButtons());
        jobTable.getColumnModel().getColumn(0).setMinWidth(0);
        jobTable.getColumnModel().getColumn(0).setMaxWidth(0);
        panel.add(new JScrollPane(jobTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);

        JButton applyBtn = new JButton("Apply with Resume");
        applyBtn.setBackground(new Color(34, 197, 94));
        applyBtn.setForeground(Color.WHITE);
        applyBtn.addActionListener(e -> applyToJob());
        btnPanel.add(applyBtn);

        saveBtn = new JButton("Save Job");
        saveBtn.setBackground(new Color(180, 180, 180));
        saveBtn.setForeground(Color.BLACK);
        saveBtn.addActionListener(e -> toggleSaveJob());
        saveBtn.setEnabled(false);
        btnPanel.add(saveBtn);

        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createMyApplicationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 250));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(new JLabel("Track your job applications:"));

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(59, 130, 246));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.addActionListener(e -> refreshMyApplications());
        topPanel.add(refreshBtn);
        panel.add(topPanel, BorderLayout.NORTH);

        String[] cols = {"Job Title", "Company", "Status"};
        myAppsModel = new DefaultTableModel(cols, 0);
        myAppsTable = new JTable(myAppsModel);
        myAppsTable.setRowHeight(35);
        myAppsTable.setSelectionBackground(new Color(59, 130, 246));
        panel.add(new JScrollPane(myAppsTable), BorderLayout.CENTER);

        JPanel legendPanel = new JPanel();
        legendPanel.setBackground(Color.WHITE);
        legendPanel.add(new JLabel("Applied"));
        legendPanel.add(new JLabel(" | "));
        legendPanel.add(new JLabel("Reviewed"));
        legendPanel.add(new JLabel(" | "));
        legendPanel.add(new JLabel("Accepted"));
        legendPanel.add(new JLabel(" | "));
        legendPanel.add(new JLabel("Rejected"));
        panel.add(legendPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshMyApplications() {
        myAppsModel.setRowCount(0);
        User currentUser = DataManager.getInstance().getCurrentUser();
        if (currentUser == null) return;
        List<JobApplication> apps = DataManager.getInstance().getApplicationsByUser(currentUser.getUsername());
        for (JobApplication app : apps) {
            myAppsModel.addRow(new Object[]{
                app.getJob().getTitle(),
                app.getJob().getCompanyUsername(),
                app.getStatus()
            });
        }
    }

    private void updateButtons() {
        int row = jobTable.getSelectedRow();
        if (row != -1) {
            String jobId = (String) tableModel.getValueAt(row, 0);
            Applicant applicant = (Applicant) DataManager.getInstance().getCurrentUser();
            saveBtn.setText(applicant.isJobSaved(jobId) ? "Unsave Job" : "Save Job");
            saveBtn.setEnabled(true);
        } else {
            saveBtn.setEnabled(false);
        }
    }

    private void refreshJobList() {
        tableModel.setRowCount(0);
        List<Job> allJobs = DataManager.getInstance().getJobs();
        List<Job> filtered = searchStrategy.search(allJobs, searchField.getText());
        Applicant applicant = (Applicant) DataManager.getInstance().getCurrentUser();
        boolean showSaved = showSavedBtn.isSelected();
        for (Job j : filtered) {
            if (j.isActive()) {
                if (showSaved && !applicant.isJobSaved(j.getId())) continue;
                tableModel.addRow(new Object[]{j.getId(), j.getTitle(), j.getCompanyUsername(), 
                    j.getDescription(), j.getRequirements()});
            }
        }
    }

    private void toggleSaveJob() {
        int row = jobTable.getSelectedRow();
        if (row == -1) return;
        String jobId = (String) tableModel.getValueAt(row, 0);
        Applicant applicant = (Applicant) DataManager.getInstance().getCurrentUser();
        if (applicant.isJobSaved(jobId)) {
            applicant.removeSavedJob(jobId);
        } else {
            applicant.saveJob(jobId);
        }
        updateButtons();
        if (showSavedBtn.isSelected()) refreshJobList();
    }

    private void showProfile() {
        Applicant applicant = (Applicant) DataManager.getInstance().getCurrentUser();
        JTextField emailField = new JTextField(applicant.getEmail());
        JTextField resumeField = new JTextField(applicant.getResumePath());
        JButton browseBtn = new JButton("Browse");
        browseBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                resumeField.setText(fc.getSelectedFile().getAbsolutePath());
            }
        });
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Default Resume:"));
        panel.add(resumeField);
        panel.add(new JLabel(""));
        panel.add(browseBtn);
        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Profile", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            applicant.setEmail(emailField.getText());
            applicant.setResumePath(resumeField.getText());
            JOptionPane.showMessageDialog(this, "Profile Updated!");
        }
    }

    private void applyToJob() {
        int row = jobTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a job first");
            return;
        }
        String jobId = (String) tableModel.getValueAt(row, 0);
        Job selectedJob = DataManager.getInstance().getJobs().stream()
            .filter(j -> j.getId().equals(jobId)).findFirst().orElse(null);
        if (selectedJob == null) return;

        User currentUser = DataManager.getInstance().getCurrentUser();
        Applicant applicant = (Applicant) currentUser;
        String resumeToUse = applicant.getResumePath();

        int choice = JOptionPane.YES_OPTION;
        if (resumeToUse != null && !resumeToUse.isEmpty()) {
            choice = JOptionPane.showConfirmDialog(this, "Use default resume?\n" + resumeToUse,
                "Confirm Resume", JOptionPane.YES_NO_CANCEL_OPTION);
        } else {
            choice = JOptionPane.NO_OPTION;
        }
        if (choice == JOptionPane.CANCEL_OPTION) return;
        if (choice == JOptionPane.NO_OPTION) {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                resumeToUse = fc.getSelectedFile().getAbsolutePath();
            } else {
                return;
            }
        }
        if (resumeToUse == null || resumeToUse.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Resume is required!");
            return;
        }

        try {
            java.io.File source = new java.io.File(resumeToUse);
            java.io.File destDir = new java.io.File(RESUME_DIR);
            if (!destDir.exists()) destDir.mkdir();
            String ext = "";
            int i = source.getName().lastIndexOf('.');
            if (i > 0) ext = source.getName().substring(i);
            String destName = applicant.getUsername() + "_" + System.currentTimeMillis() + ext;
            java.io.File dest = new java.io.File(destDir, destName);
            java.nio.file.Files.copy(source.toPath(), dest.toPath(), 
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            resumeToUse = dest.getAbsolutePath();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error uploading resume: " + e.getMessage());
            return;
        }

        JobApplication app = new JobApplication(currentUser.getUsername(), selectedJob, resumeToUse);
        DataManager.getInstance().addApplication(app);
        JOptionPane.showMessageDialog(this, "Applied successfully!");
    }
}
