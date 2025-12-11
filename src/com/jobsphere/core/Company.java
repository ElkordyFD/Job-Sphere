package com.jobsphere.core;

/**
 * Company user role.
 */
public class Company extends User {
    private String companyName;

    public Company(String username, String password, String email) {
        super(username, password, email);
    }

    public void setCompanyName(String name) { this.companyName = name; }
    public String getCompanyName() { return companyName; }

    @Override
    public String getRole() {
        return "COMPANY";
    }
}
