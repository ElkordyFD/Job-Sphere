package com.jobsphere.core;

/**
 * State Pattern: Application has been rejected (final state).
 */
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
