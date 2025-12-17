package com.jobsphere.core;

/**
 * State Pattern: Application has been reviewed by company.
 */
public class ReviewedState implements ApplicationState {
    @Override
    public void next(JobApplication application) {
        // Can move to Accepted or Rejected, defaulting to Accepted for simple flow
        application.setState(new AcceptedState());
    }

    @Override
    public String getStatusName() {
        return "Reviewed";
    }
}
