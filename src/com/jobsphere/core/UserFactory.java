package com.jobsphere.core;

/**
 * Factory Pattern: Creates instances of User subclasses.
 */
public class UserFactory {
    public static User createUser(String type, String username, String password, String email) {
        if (type.equalsIgnoreCase("APPLICANT")) {
            return new Applicant(username, password, email);
        } else if (type.equalsIgnoreCase("COMPANY")) {
            return new Company(username, password, email);
        }
        throw new IllegalArgumentException("Unknown user type: " + type);
    }
}
