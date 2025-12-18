package com.jobsphere.core;

import java.util.List;

public interface UserRepository {
    void add(User user);

    User findByUsername(String username);

    List<User> findByRole(String role);
}
