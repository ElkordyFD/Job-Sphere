package com.jobsphere.core;

/**
 * State Pattern: Interface for application states.
 */
public interface ApplicationState {
    void next(JobApplication application);
    String getStatusName();
}
