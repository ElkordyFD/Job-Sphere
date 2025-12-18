package com.jobsphere.core;

import java.util.ArrayList;
import java.util.List;

public class InMemoryApplicationRepository implements ApplicationRepository {
    private final List<JobApplication> applications = new ArrayList<>();

    @Override
    public void add(JobApplication application) {
        applications.add(application);
    }

    @Override
    public List<JobApplication> findByJobId(String jobId) {
        List<JobApplication> result = new ArrayList<>();
        for (JobApplication app : applications) {
            if (app.getJob().getId().equals(jobId)) {
                result.add(app);
            }
        }
        return result;
    }

    @Override
    public List<JobApplication> findByUsername(String username) {
        List<JobApplication> result = new ArrayList<>();
        for (JobApplication app : applications) {
            if (app.getApplicantUsername().equals(username)) {
                result.add(app);
            }
        }
        return result;
    }

}
