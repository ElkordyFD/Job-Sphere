package com.jobsphere.core;

import java.util.List;

public interface JobRepository {
    void add(Job job);

    void remove(String jobId);

    Job findById(String jobId);

    List<Job> findAll();

    List<Job> findByCompany(String companyUsername);
}
