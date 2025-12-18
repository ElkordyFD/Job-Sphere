package com.jobsphere.core;
import java.util.*;

public class Applicant extends User {
    private String resumePath;
    private final List<String> savedJobIds;

    public Applicant(String username, String password, String email) {
        super(username, password, email);
        this.savedJobIds = new ArrayList<>();
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

    @Override
    public String getRole() {
        return "APPLICANT";
    }
}
