package com.jobsphere.core;

public class UserFactory {

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
