package com.jobsphere.core;

/**
 * State Pattern: Concrete states.
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

class ReviewedState implements ApplicationState {
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

class AcceptedState implements ApplicationState {
    @Override
    public void next(JobApplication application) {
        // Final state
    }

    @Override
    public String getStatusName() {
        return "Accepted";
    }
}

class RejectedState implements ApplicationState {
    @Override
    public void next(JobApplication application) {
        // Final state
    }

    @Override
    public String getStatusName() {
        return "Rejected";
    }
}
