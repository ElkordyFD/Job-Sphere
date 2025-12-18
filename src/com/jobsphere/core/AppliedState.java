package com.jobsphere.core;

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
