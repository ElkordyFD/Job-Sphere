package com.jobsphere.core;

public class Company extends User {

    public Company(String username, String password, String email) {
        super(username, password, email);
    }

    @Override
    public String getRole() {
        return "COMPANY";
    }
}
