package com.jobsphere.core;

import java.util.List;

/**
 * Service class for job operations.
 * Single Responsibility: Handles business logic for jobs.
 */
public class JobService {
    private final JobRepository jobRepo;
    private final NotificationService notificationService;

    public JobService(JobRepository jobRepo, NotificationService notificationService) {
        this.jobRepo = jobRepo;
        this.notificationService = notificationService;
    }

    /**
     * Post a new job and notify observers.
     */
    public Job postJob(String title, String description, String requirements, String companyUsername) {
        Job job = new JobBuilder()
                .setTitle(title)
                .setDescription(description)
                .setRequirements(requirements)
                .setCompanyUsername(companyUsername)
                .build();

        jobRepo.add(job);

        // Notify observers
        notificationService.notifyObservers("New Job Posted: " + title + " by " + companyUsername);

        return job;
    }

    /**
     * Get all active jobs.
     */
    public List<Job> getActiveJobs() {
        return jobRepo.findAll().stream()
                .filter(Job::isActive)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Get jobs by company.
     */
    public List<Job> getJobsByCompany(String companyUsername) {
        return jobRepo.findByCompany(companyUsername);
    }

    /**
     * Toggle job active status.
     */
    public void toggleJobStatus(Job job) {
        job.setActive(!job.isActive());
    }

    /**
     * Update job title.
     */
    public void updateJobTitle(Job job, String newTitle) {
        job.setTitle(newTitle);
    }

    /**
     * Remove a job.
     */
    public void removeJob(String jobId) {
        jobRepo.remove(jobId);
    }

    /**
     * Find job by ID.
     */
    public Job findById(String jobId) {
        return jobRepo.findById(jobId);
    }
}
