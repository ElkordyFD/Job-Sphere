package com.jobsphere.core;

public class RejectedState implements ApplicationState {
    @Override
    public void next(JobApplication application) {
        // Final state - no transition
    }

    @Override
    public String getStatusName() {
        return "Rejected";
    }
}
