package com.jobsphere.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Singleton Pattern: Manages global application data (Users, Jobs).
 */
public class DataManager {
    private static DataManager instance;

    private List<User> users;
    private List<Job> jobs;
    private User currentUser;

    private DataManager() {
        users = new ArrayList<>();
        jobs = new ArrayList<>();
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    // User Management
    public void registerUser(User user) {
        users.add(user);
    }

    public User login(String username, String password) {
        Optional<User> user = users.stream()
                .filter(u -> u.getUsername().equals(username) && u.checkPassword(password))
                .findFirst();

        if (user.isPresent()) {
            currentUser = user.get();
            return currentUser;
        }
        return null;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    // Job Management
    public void addJob(Job job) {
        jobs.add(job);
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public List<Job> getJobsByCompany(String companyUsername) {
        List<Job> companyJobs = new ArrayList<>();
        for (Job j : jobs) {
            if (j.getCompanyUsername().equals(companyUsername)) {
                companyJobs.add(j);
            }
        }
        return companyJobs;
    }

    // Application Management
    private List<JobApplication> applications = new ArrayList<>();

    public void addApplication(JobApplication app) {
        applications.add(app);
    }

    public List<JobApplication> getApplicationsForJob(String jobId) {
        List<JobApplication> jobApps = new ArrayList<>();
        for (JobApplication app : applications) {
            if (app.getJob().getId().equals(jobId)) {
                jobApps.add(app);
            }
        }
        return jobApps;
    }

    public List<JobApplication> getApplicationsByUser(String username) {
        List<JobApplication> userApps = new ArrayList<>();
        for (JobApplication app : applications) {
            if (app.getApplicantUsername().equals(username)) {
                userApps.add(app);
            }
        }
        return userApps;
    }
}
