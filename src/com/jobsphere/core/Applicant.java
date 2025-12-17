package com.jobsphere.core;

public class Applicant extends User {
    private String resumePath;
    private java.util.List<String> savedJobIds;

    public Applicant(String username, String password, String email) {
        super(username, password, email);
        this.savedJobIds = new java.util.ArrayList<>();
    }

    public void setResumePath(String path) {
        this.resumePath = path;
    }

    public String getResumePath() {
        return resumePath;
    }

    public void saveJob(String jobId) {
        if (!savedJobIds.contains(jobId)) {
            savedJobIds.add(jobId);
        }
    }

    public void removeSavedJob(String jobId) {
        savedJobIds.remove(jobId);
    }

    public boolean isJobSaved(String jobId) {
        return savedJobIds.contains(jobId);
    }

    public java.util.List<String> getSavedJobIds() {
        return new java.util.ArrayList<>(savedJobIds);
    }

    @Override
    public String getRole() {
        return "APPLICANT";
    }
}
