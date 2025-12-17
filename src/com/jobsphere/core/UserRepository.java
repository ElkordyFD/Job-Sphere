package com.jobsphere.core;

import java.util.List;

/**
 * Repository interface for User management.
 * Follows Interface Segregation and Dependency Inversion principles.
 */
public interface UserRepository {
    void add(User user);

    User findByUsername(String username);

    List<User> findAll();

    List<User> findByRole(String role);
}
