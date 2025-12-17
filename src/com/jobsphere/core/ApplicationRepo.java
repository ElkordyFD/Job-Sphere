package com.jobsphere.core;

import java.util.ArrayList;
import java.util.List;

public class ApplicationRepo {
    private static ApplicationRepo instance;
    private List<JobApplication> applications = new ArrayList<>();

    private ApplicationRepo() {}

    public static synchronized ApplicationRepo getInstance() {
        if (instance == null) {
            instance = new ApplicationRepo();
        }
        return instance;
    }

    public void addApplication(JobApplication app) {
        applications.add(app);
    }

    public List<JobApplication> getApplicationsForJob(String jobId) {
        return applications.stream()
                .filter(a -> a.getJob().getId().equals(jobId))
                .toList();
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

