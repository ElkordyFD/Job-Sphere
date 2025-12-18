package com.jobsphere.core;

public class ReviewedState implements ApplicationState {
    @Override
    public void next(JobApplication application) {
        application.setState(new AcceptedState());
    }

    @Override
    public String getStatusName() {
        return "Reviewed";
    }
}
