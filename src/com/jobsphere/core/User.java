package com.jobsphere.core;

/**
 * Abstract User class representing a generic user in the system.
 */
public abstract class User {
    protected String username;
    protected String password;
    protected String email;

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() { return username; }
    public boolean checkPassword(String password) { return this.password.equals(password); }
    public abstract String getRole();
}
