package com.jobsphere.core;

import java.util.ArrayList;
import java.util.List;

public class InMemoryJobRepository implements JobRepository {
    private final List<Job> jobs = new ArrayList<>();

    @Override
    public void add(Job job) {
        jobs.add(job);
    }

    @Override
    public void remove(String jobId) {
        jobs.removeIf(j -> j.getId().equals(jobId));
    }

    @Override
    public Job findById(String jobId) {
        return jobs.stream()
                .filter(j -> j.getId().equals(jobId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Job> findAll() {
        return new ArrayList<>(jobs);
    }

    @Override
    public List<Job> findByCompany(String companyUsername) {
        List<Job> result = new ArrayList<>();
        for (Job j : jobs) {
            if (j.getCompanyUsername().equals(companyUsername)) {
                result.add(j);
            }
        }
        return result;
    }
}
