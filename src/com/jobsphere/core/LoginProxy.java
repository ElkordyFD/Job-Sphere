package com.jobsphere.core;



public class LoginProxy implements LoginService {

    private final LoginService realLoginService;
    private int attempts = 0;
    private static final int MAX_ATTEMPTS = 3;

    public LoginProxy(LoginService realLoginService) {
        this.realLoginService = realLoginService;
    }

    @Override
    public User login(String username, String password) {

        if (attempts >= MAX_ATTEMPTS) {
            throw new SecurityException("Too many login attempts");
        }

        User user = realLoginService.login(username, password);

        if (user == null) {
            attempts++;
            System.out.println("[Proxy] Failed login attempt " + attempts);
        } else {
            attempts = 0; // reset on success
        }

        return user;
    }
}


