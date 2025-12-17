package com.jobsphere.core;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Factory Pattern: Creates instances of User subclasses.
 * Open/Closed Principle: New user types can be registered without modifying
 * this class.
 */
public class UserFactory {

    // Function that takes (username, password, email) and returns a User
    @FunctionalInterface
    public interface UserCreator {
        User create(String username, String password, String email);
    }

    // Registry of user creators - extensible without modification (OCP)
    private static final Map<String, UserCreator> creators = new HashMap<>();

    // Static initializer - register default types
    static {
        registerType("APPLICANT", Applicant::new);
        registerType("COMPANY", Company::new);
    }

    /**
     * Register a new user type (Open for extension).
     * 
     * @param type    The type identifier (e.g., "ADMIN")
     * @param creator Function to create the user
     */
    public static void registerType(String type, UserCreator creator) {
        creators.put(type.toUpperCase(), creator);
    }

    /**
     * Create a user of the specified type.
     * 
     * @param type     User type (case-insensitive)
     * @param username Username
     * @param password Password
     * @param email    Email
     * @return Created user instance
     * @throws IllegalArgumentException if type is not registered
     */
    public static User createUser(String type, String username, String password, String email) {
        UserCreator creator = creators.get(type.toUpperCase());
        if (creator == null) {
            throw new IllegalArgumentException("Unknown user type: " + type +
                    ". Registered types: " + creators.keySet());
        }
        return creator.create(username, password, email);
    }

    /**
     * Check if a user type is registered.
     */
    public static boolean isTypeRegistered(String type) {
        return creators.containsKey(type.toUpperCase());
    }
}
