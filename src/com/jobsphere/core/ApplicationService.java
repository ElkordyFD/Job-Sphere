package com.jobsphere.core;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Service class for job application operations.
 * Single Responsibility: Handles business logic for applications.
 * Extracted from UI to maintain separation of concerns.
 */
public class ApplicationService {
    private static final String RESUME_DIR = "resumes";

    private final ApplicationRepository applicationRepo;
    private final NotificationService notificationService;

    public ApplicationService(ApplicationRepository applicationRepo, NotificationService notificationService) {
        this.applicationRepo = applicationRepo;
        this.notificationService = notificationService;
    }

    /**
     * Submit a job application with resume.
     * 
     * @param applicant  The applicant
     * @param job        The job to apply for
     * @param resumePath Path to the resume file
     * @return The created application
     * @throws Exception if resume upload fails
     */
    public JobApplication submitApplication(Applicant applicant, Job job, String resumePath) throws Exception {
        // Copy resume to local directory
        String savedResumePath = copyResumeToStorage(applicant.getUsername(), resumePath);

        // Create and save application
        JobApplication application = new JobApplication(applicant.getUsername(), job, savedResumePath);
        applicationRepo.add(application);

        return application;
    }

    /**
     * Copy resume file to application storage.
     */
    private String copyResumeToStorage(String username, String sourcePath) throws Exception {
        File source = new File(sourcePath);
        File destDir = new File(RESUME_DIR);
        if (!destDir.exists()) {
            destDir.mkdir();
        }

        String ext = "";
        int i = source.getName().lastIndexOf('.');
        if (i > 0) {
            ext = source.getName().substring(i);
        }

        String destName = username + "_" + System.currentTimeMillis() + ext;
        File dest = new File(destDir, destName);

        Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return dest.getAbsolutePath();
    }

    /**
     * Get all applications for a specific job.
     */
    public List<JobApplication> getApplicationsForJob(String jobId) {
        return applicationRepo.findByJobId(jobId);
    }

    /**
     * Get all applications by a user.
     */
    public List<JobApplication> getApplicationsByUser(String username) {
        return applicationRepo.findByUsername(username);
    }

    /**
     * Move application to next state.
     */
    public void advanceApplication(JobApplication application) {
        application.next();
    }
}
