package com.jobsphere.core;

import java.util.List;

/**
 * Singleton Pattern: Central access point for all repositories and services.
 * Now delegates to specialized repositories (SRP compliant).
 * Acts as a Facade for the data layer.
 */
public class DataManager {
    private static DataManager instance;

    // Repositories (Dependency Inversion - depends on interfaces)
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    // Services
    private final SessionManager sessionManager;
    private final NotificationService notificationService;

    private DataManager() {
        // Default implementations (can be swapped for testing or different storage)
        this.userRepository = new InMemoryUserRepository();
        this.jobRepository = new InMemoryJobRepository();
        this.applicationRepository = new InMemoryApplicationRepository();
        this.sessionManager = new SessionManager();
        this.notificationService = new NotificationService();
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    // ============ Repository Accessors ============
    public UserRepository getUserRepository() {
        return userRepository;
    }

    public JobRepository getJobRepository() {
        return jobRepository;
    }

    public ApplicationRepository getApplicationRepository() {
        return applicationRepository;
    }

    // ============ Service Accessors ============
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public NotificationService getNotificationService() {
        return notificationService;
    }

    // ============ Convenience Methods (Facade) ============
    // These delegate to repositories for backward compatibility

    public void registerUser(User user) {
        userRepository.add(user);
    }

    public User login(String username, String password) {
        return sessionManager.login(userRepository, username, password);
    }

    public void logout() {
        sessionManager.logout();
    }

    public User getCurrentUser() {
        return sessionManager.getCurrentUser();
    }

    public void addJob(Job job) {
        jobRepository.add(job);
    }

    public List<Job> getJobs() {
        return jobRepository.findAll();
    }

    public List<Job> getJobsByCompany(String companyUsername) {
        return jobRepository.findByCompany(companyUsername);
    }

    public void removeJob(String jobId) {
        jobRepository.remove(jobId);
    }

    public List<User> getAllApplicants() {
        return userRepository.findByRole("APPLICANT");
    }

    public void addApplication(JobApplication app) {
        applicationRepository.add(app);
    }

    public List<JobApplication> getApplicationsForJob(String jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    public List<JobApplication> getApplicationsByUser(String username) {
        return applicationRepository.findByUsername(username);
    }
}
