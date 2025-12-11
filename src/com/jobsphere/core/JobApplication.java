package com.jobsphere.core;

/**
 * Represents a job application.
 * Uses State Pattern to manage status.
 */
public class JobApplication {
    private String applicantUsername;
    private Job job;
    private ApplicationState state;

    private String resumePath;

    public JobApplication(String applicantUsername, Job job, String resumePath) {
        this.applicantUsername = applicantUsername;
        this.job = job;
        this.resumePath = resumePath;
        this.state = new AppliedState(); // Initial state
    }

    public String getResumePath() {
        return resumePath;
    }

    public void setState(ApplicationState state) {
        this.state = state;
    }

    public String getStatus() {
        return state.getStatusName();
    }

    public void next() {
        state.next(this);
    }

    public String getApplicantUsername() {
        return applicantUsername;
    }

    public Job getJob() {
        return job;
    }
}
