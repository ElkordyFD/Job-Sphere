package com.jobsphere.core;

public class LoginProxy implements LoginService {

    private final LoginService realLoginService;

    public LoginProxy(LoginService realLoginService) {
        this.realLoginService = realLoginService;
    }

    @Override
    public User login(String username, String password) {
        System.out.println("[LoginProxy] Login attempt for user: " + username);
        return realLoginService.login(username, password);
    }
}
