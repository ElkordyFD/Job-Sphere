package com.jobsphere.ui;

import com.jobsphere.core.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ApplicantPanel extends JPanel implements Observer {
    private MainFrame mainFrame;
    private JTable jobTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private SearchStrategy searchStrategy;

    private JToggleButton showSavedBtn;
    private JButton saveBtn;
    private static final String RESUME_DIR = "resumes";

    // Notification State
    private JButton notificationBtn;
    private int notificationCount = 0;
    private java.util.List<String> notifications = new java.util.ArrayList<>();

    public ApplicantPanel(MainFrame frame) {
        this.mainFrame = frame;
        this.searchStrategy = new KeywordSearchStrategy();

        // Register for Notifications
        DataManager.getInstance().getNotificationService().addObserver(this);

        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        header.setBackground(new Color(70, 130, 180)); // Steel Blue

        JLabel title = new JLabel("Applicant Dashboard");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JButton profileBtn = new JButton("Profile");
        styleHeaderButton(profileBtn);
        profileBtn.addActionListener(e -> showProfile());

        JButton logoutBtn = new JButton("Logout");
        styleHeaderButton(logoutBtn);
        logoutBtn.addActionListener(e -> {
            DataManager.getInstance().logout();
            mainFrame.showCard("LOGIN");
        });

        // Notification Button
        notificationBtn = new JButton("Notifications (0)");
        notificationBtn.setBackground(new Color(255, 193, 7)); // Amber/Gold
        notificationBtn.setForeground(Color.BLACK);
        notificationBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        notificationBtn.setFocusPainted(false);
        notificationBtn.addActionListener(e -> showNotifications());

        header.add(title);
        header.add(Box.createHorizontalStrut(200)); // Adjusted spacing
        header.add(notificationBtn);
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
        String[] cols = { "ID", "Title", "Company", "Description", "Requirements" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jobTable = new JTable(tableModel);
        styleTable(jobTable);
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
            Applicant applicant = (Applicant) DataManager.getInstance().getCurrentUser();
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
        List<Job> allJobs = DataManager.getInstance().getJobs();
        List<Job> filtered = searchStrategy.search(allJobs, searchField.getText());

        Applicant applicant = (Applicant) DataManager.getInstance().getCurrentUser();
        boolean showSaved = showSavedBtn.isSelected();

        for (Job j : filtered) {
            if (j.isActive()) {
                if (showSaved && !applicant.isJobSaved(j.getId())) {
                    continue;
                }
                tableModel.addRow(new Object[] { j.getId(), j.getTitle(), j.getCompanyUsername(), j.getDescription(),
                        j.getRequirements() });
            }
        }
    }

    private void toggleSaveJob() {
        int row = jobTable.getSelectedRow();
        if (row == -1)
            return;

        String jobId = (String) tableModel.getValueAt(row, 0);
        Applicant applicant = (Applicant) DataManager.getInstance().getCurrentUser();

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
        Job selectedJob = DataManager.getInstance().getJobs().stream()
                .filter(j -> j.getId().equals(jobId)).findFirst().orElse(null);

        if (selectedJob != null) {
            User currentUser = DataManager.getInstance().getCurrentUser();
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
            DataManager.getInstance().addApplication(app);
            JOptionPane.showMessageDialog(this, "Applied successfully!");
        }
    }

    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.setSelectionBackground(new Color(100, 149, 237)); // Cornflower Blue
        table.setSelectionForeground(Color.WHITE);

        // Hide ID Column (Index 0)
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
    }

    private void styleHeaderButton(JButton btn) {
        btn.setBackground(new Color(255, 255, 255));
        btn.setForeground(new Color(70, 130, 180));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
    }

    @Override
    public void update(String message) {
        notificationCount++;
        notifications.add(message);
        notificationBtn.setText("Notifications (" + notificationCount + ")");

        // Refresh valid UI components
        if (showSavedBtn != null && showSavedBtn.isVisible()) {
            refreshJobList();
            // Optional to also show popup, but let's stick to the Bell as requested
            // JOptionPane.showMessageDialog(this, message, "New Notification",
            // JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showNotifications() {
        if (notifications.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No new notifications.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (String msg : notifications) {
                sb.append("- ").append(msg).append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Notifications", JOptionPane.INFORMATION_MESSAGE);

            // Reset Count
            notificationCount = 0;
            notificationBtn.setText("Notifications (0)");
            // clear list if you want, or just keep history. Let's keep history but reset
            // count (unread).
        }
    }
}
