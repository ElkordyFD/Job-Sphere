package com.jobsphere.core;

import java.util.ArrayList;
import java.util.List;

public class JobRepo {
    private static JobRepo instance;
    private List<Job> jobs = new ArrayList<>();

    private JobRepo() {}

    public static synchronized JobRepo getInstance() {
        if (instance == null) {
            instance = new JobRepo();
        }
        return instance;
    }

    public void addJob(Job job) {
        jobs.add(job);
    }

    public List<Job> getJobs() {
        return jobs;
    }

        public void removeJob(String jobId) {
        jobs.removeIf(j -> j.getId().equals(jobId));
    }

    public List<Job> getJobsByCompany(String companyName) {
        return jobs.stream()
                .filter(j -> j.getCompanyUsername().equals(companyName))
                .toList();
    }


}

