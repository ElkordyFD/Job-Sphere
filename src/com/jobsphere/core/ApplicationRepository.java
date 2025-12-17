package com.jobsphere.core;

import java.util.List;

/**
 * Repository interface for JobApplication management.
 * Follows Interface Segregation and Dependency Inversion principles.
 */
public interface ApplicationRepository {
    void add(JobApplication application);

    List<JobApplication> findByJobId(String jobId);

    List<JobApplication> findByUsername(String username);

    List<JobApplication> findAll();
}
