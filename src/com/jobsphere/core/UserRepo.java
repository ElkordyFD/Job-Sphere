package com.jobsphere.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepo {
    private static UserRepo instance;
    private List<User> users = new ArrayList<>();

    private UserRepo() {}

    public static synchronized UserRepo getInstance() {
        if (instance == null) {
            instance = new UserRepo();
        }
        return instance;
    }

    public void registerUser(User user) {
        users.add(user);
    }

    public Optional<User> findByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }

    public List<User> getAllApplicants() {
        List<User> applicants = new ArrayList<>();
        for (User u : users) {
            if (u.getRole().equals("APPLICANT")) {
                applicants.add(u);
            }
        }
        return applicants;
    }
}

