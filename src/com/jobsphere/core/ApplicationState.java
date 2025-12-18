package com.jobsphere.core;

public interface ApplicationState {
    void next(JobApplication application);
    String getStatusName();
}
