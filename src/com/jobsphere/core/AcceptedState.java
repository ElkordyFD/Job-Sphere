package com.jobsphere.core;

/**
 * State Pattern: Application has been accepted (final state).
 */
public class AcceptedState implements ApplicationState {
    @Override
    public void next(JobApplication application) {
        // Final state - no transition
    }

    @Override
    public String getStatusName() {
        return "Accepted";
    }
}
