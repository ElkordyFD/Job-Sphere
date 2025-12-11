package com.jobsphere.core;

/**
 * Represents a Job Listing.
 */
public class Job {
    private String id;
    private String title;
    private String description;
    private String companyUsername;
    private String requirements;
    private boolean isActive;

    public Job(String id, String title, String description, String companyUsername, String requirements) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.companyUsername = companyUsername;
        this.requirements = requirements;
        this.isActive = true;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCompanyUsername() {
        return companyUsername;
    }

    public String getRequirements() {
        return requirements;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    @Override
    public String toString() {
        return title + " at " + companyUsername;
    }
}
