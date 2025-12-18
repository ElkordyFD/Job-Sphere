package com.jobsphere.core;

import java.util.List;

public interface ApplicationRepository {
    void add(JobApplication application);

    List<JobApplication> findByJobId(String jobId);

    List<JobApplication> findByUsername(String username);

}
