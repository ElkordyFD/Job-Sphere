package com.jobsphere.core;

import java.util.List;

public class DataManager {
    private static DataManager instance;

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    private final SessionManager sessionManager;
    private final NotificationService notificationService;
    private final LoginService loginService; // Proxy Pattern

    private DataManager() {
        this.userRepository = new InMemoryUserRepository();
        this.jobRepository = new InMemoryJobRepository();
        this.applicationRepository = new InMemoryApplicationRepository();
        this.sessionManager = new SessionManager();
        this.notificationService = new NotificationService();

        // Proxy Pattern: LoginProxy wraps the RealLoginService
        RealLoginService realLoginService = new RealLoginService(userRepository, sessionManager);
        this.loginService = new LoginProxy(realLoginService);
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public NotificationService getNotificationService() {
        return notificationService;
    }

    public void registerUser(User user) {
        userRepository.add(user);
    }

    public User login(String username, String password) {
        return loginService.login(username, password);
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
