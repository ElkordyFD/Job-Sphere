package com.jobsphere.core;

/**
 * State Pattern: Initial state when application is submitted.
 */
public class AppliedState implements ApplicationState {
    @Override
    public void next(JobApplication application) {
        application.setState(new ReviewedState());
    }

    @Override
    public String getStatusName() {
        return "Applied";
    }
}
