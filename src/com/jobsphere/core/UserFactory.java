package com.jobsphere.core;

/**
 * Factory Pattern: Creates instances of User subclasses.
 * Simple factory implementation using switch statement.
 */
public class UserFactory {

    /**
     * Create a user of the specified type.
     * 
     * @param type     User type ("APPLICANT" or "COMPANY")
     * @param username Username
     * @param password Password
     * @param email    Email
     * @return Created user instance
     * @throws IllegalArgumentException if type is unknown
     */
    public static User createUser(String type, String username, String password, String email) {
        if (type == null) {
            throw new IllegalArgumentException("User type cannot be null");
        }

        switch (type.toUpperCase()) {
            case "APPLICANT":
                return new Applicant(username, password, email);
            case "COMPANY":
                return new Company(username, password, email);
            default:
                throw new IllegalArgumentException("Unknown user type: " + type +
                        ". Valid types are: APPLICANT, COMPANY");
        }
    }
}
