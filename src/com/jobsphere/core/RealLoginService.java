package com.jobsphere.core;


public class RealLoginService implements LoginService {
    private final UserRepository userRepository;
    private final SessionManager sessionManager;

    public RealLoginService(UserRepository userRepository, SessionManager sessionManager) {
        this.userRepository = userRepository;
        this.sessionManager = sessionManager;
    }

    @Override
    public User login(String username, String password) {
        return sessionManager.login(userRepository, username, password);
    }
}
