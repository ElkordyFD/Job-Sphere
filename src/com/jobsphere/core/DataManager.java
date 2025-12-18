package com.jobsphere.core;

import java.util.List;

public class DataManager {
    private static DataManager instance;

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    private final LoginService loginService;
    private User currentUser;

    private DataManager() {
        this.userRepository = new InMemoryUserRepository();
        this.jobRepository = new InMemoryJobRepository();
        this.applicationRepository = new InMemoryApplicationRepository();

        RealLoginService realLoginService = new RealLoginService(userRepository);
        this.loginService = new LoginProxy(realLoginService);
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    // User operations
    public void registerUser(User user) {
        userRepository.add(user);
    }

    public User login(String username, String password) {
        User user = loginService.login(username, password);
        if (user != null) {
            this.currentUser = user;
        }
        return user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public List<User> getAllApplicants() {
        return userRepository.findByRole("APPLICANT");
    }

    // Job operations
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

    // Application operations
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
