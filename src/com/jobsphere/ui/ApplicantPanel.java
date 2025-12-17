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

    public ApplicantPanel(MainFrame frame) {
        this.mainFrame = frame;
        this.searchStrategy = new KeywordSearchStrategy();
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(new Color(100, 149, 237));
        JLabel title = new JLabel("Applicant Dashboard");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JButton profileBtn = new JButton("Profile");
        profileBtn.addActionListener(e -> showProfile());

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            DataManager.getInstance().getAuthService().logout();
            mainFrame.showCard("LOGIN");
        });

        header.add(title);
        header.add(Box.createHorizontalStrut(300));
        header.add(profileBtn);
        header.add(logoutBtn);
        add(header, BorderLayout.NORTH);

        // Center - Job List
        JPanel centerPanel = new JPanel(new BorderLayout());

        // Search Bar
        JPanel searchPanel = new JPanel();
        searchField = new JTextField(15);
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> refreshJobList());

        showSavedBtn = new JToggleButton("Show Saved Only");
        showSavedBtn.addActionListener(e -> refreshJobList());

        searchPanel.add(new JLabel("Keywords:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(showSavedBtn);
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] cols = { "ID", "Title", "Company", "Description" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jobTable = new JTable(tableModel);
        jobTable.getSelectionModel().addListSelectionListener(e -> updateButtons());
        centerPanel.add(new JScrollPane(jobTable), BorderLayout.CENTER);

        // Buttons Panel
        JPanel btnPanel = new JPanel();

        JButton applyBtn = new JButton("Apply with Resume");
        applyBtn.setBackground(new Color(60, 179, 113));
        applyBtn.setForeground(Color.WHITE);
        applyBtn.addActionListener(e -> applyToJob());

        saveBtn = new JButton("Save Job");
        saveBtn.addActionListener(e -> toggleSaveJob());
        saveBtn.setEnabled(false);

        btnPanel.add(applyBtn);
        btnPanel.add(saveBtn);
        centerPanel.add(btnPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        refreshJobList();
    }

    private void updateButtons() {
        int row = jobTable.getSelectedRow();
        if (row != -1) {
            String jobId = (String) tableModel.getValueAt(row, 0);
            Applicant applicant = (Applicant) DataManager.getInstance().getAuthService().getCurrentUser();
            if (applicant.isJobSaved(jobId)) {
                saveBtn.setText("Unsave Job");
            } else {
                saveBtn.setText("Save Job");
            }
            saveBtn.setEnabled(true);
        } else {
            saveBtn.setEnabled(false);
        }
    }

    private void refreshJobList() {
        tableModel.setRowCount(0);
        List<Job> allJobs = DataManager.getInstance().getJobRepository().getJobs();
        List<Job> filtered = searchStrategy.search(allJobs, searchField.getText());

        Applicant applicant = (Applicant) DataManager.getInstance().getAuthService().getCurrentUser();
        boolean showSaved = showSavedBtn.isSelected();

        for (Job j : filtered) {
            if (j.isActive()) {
                if (showSaved && !applicant.isJobSaved(j.getId())) {
                    continue;
                }
                tableModel.addRow(new Object[] { j.getId(), j.getTitle(), j.getCompanyUsername(), j.getDescription() });
            }
        }
    }

    private void toggleSaveJob() {
        int row = jobTable.getSelectedRow();
        if (row == -1)
            return;

        String jobId = (String) tableModel.getValueAt(row, 0);
        Applicant applicant = (Applicant) DataManager.getInstance().getAuthService().getCurrentUser();

        if (applicant.isJobSaved(jobId)) {
            applicant.removeSavedJob(jobId);
        } else {
            applicant.saveJob(jobId);
        }
        updateButtons();
        if (showSavedBtn.isSelected()) {
            refreshJobList();
        }
    }

    private void showProfile() {
        Applicant applicant = (Applicant) DataManager.getInstance().getAuthService().getCurrentUser();
        JTextField emailField = new JTextField(applicant.getEmail());
        JTextField resumeField = new JTextField(applicant.getResumePath());
        JButton browseBtn = new JButton("Browse");

        browseBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                resumeField.setText(fc.getSelectedFile().getAbsolutePath());
            }
        });

        JPanel panel = new JPanel(new GridLayout(3, 2));
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
        Job selectedJob = DataManager.getInstance().getJobRepository().getJobs().stream()
                .filter(j -> j.getId().equals(jobId)).findFirst().orElse(null);

        if (selectedJob != null) {
            User currentUser = DataManager.getInstance().getAuthService().getCurrentUser();
            Applicant applicant = (Applicant) currentUser;

            String resumeToUse = applicant.getResumePath();

            // If no default resume, or user wants to change it
            int choice = JOptionPane.YES_OPTION;
            if (resumeToUse != null && !resumeToUse.isEmpty()) {
                choice = JOptionPane.showConfirmDialog(this,
                        "Use default resume?\n" + resumeToUse,
                        "Confirm Resume", JOptionPane.YES_NO_CANCEL_OPTION);
            } else {
                choice = JOptionPane.NO_OPTION; // Force upload
            }

            if (choice == JOptionPane.CANCEL_OPTION)
                return;

            if (choice == JOptionPane.NO_OPTION) {
                JFileChooser fc = new JFileChooser();
                if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    resumeToUse = fc.getSelectedFile().getAbsolutePath();
                    // Optional: ask to save as default
                } else {
                    return; // Cancelled
                }
            }

            if (resumeToUse == null || resumeToUse.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Resume is required!");
                return;
            }

            // Copy resume to local directory
            try {
                java.io.File source = new java.io.File(resumeToUse);
                java.io.File destDir = new java.io.File(RESUME_DIR);
                if (!destDir.exists())
                    destDir.mkdir();

                String ext = "";
                int i = source.getName().lastIndexOf('.');
                if (i > 0)
                    ext = source.getName().substring(i);

                String destName = applicant.getUsername() + "_" + System.currentTimeMillis() + ext;
                java.io.File dest = new java.io.File(destDir, destName);

                java.nio.file.Files.copy(source.toPath(), dest.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                resumeToUse = dest.getAbsolutePath();

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error uploading resume: " + e.getMessage());
                return;
            }

            JobApplication app = new JobApplication(currentUser.getUsername(), selectedJob, resumeToUse);
            DataManager.getInstance().getApplicationRepository().addApplication(app);
            JOptionPane.showMessageDialog(this, "Applied successfully!");
        }
    }
}
