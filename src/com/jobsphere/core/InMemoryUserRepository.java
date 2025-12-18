package com.jobsphere.core;

import java.util.ArrayList;
import java.util.List;

public class InMemoryUserRepository implements UserRepository {
    private final List<User> users = new ArrayList<>();

    @Override
    public void add(User user) {
        users.add(user);
    }

    @Override
    public User findByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> findByRole(String role) {
        List<User> result = new ArrayList<>();
        for (User u : users) {
            if (u.getRole().equals(role)) {
                result.add(u);
            }
        }
        return result;
    }
}
