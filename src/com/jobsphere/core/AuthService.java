package com.jobsphere.core;

import java.util.Optional;

public class AuthService {
    private UserRepo userRepo = UserRepo.getInstance();
    private User currentUser;

    public boolean login(String username, String password) {
        Optional<User> user = userRepo.findByUsername(username);
        if (user.isPresent() && user.get().checkPassword(password)) {
            currentUser = user.get();
            return true;
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}

