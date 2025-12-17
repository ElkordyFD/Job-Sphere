package com.jobsphere.core;

import java.util.List;

/**
 * Repository interface for Job management.
 * Follows Interface Segregation and Dependency Inversion principles.
 */
public interface JobRepository {
    void add(Job job);

    void remove(String jobId);

    Job findById(String jobId);

    List<Job> findAll();

    List<Job> findByCompany(String companyUsername);
}
