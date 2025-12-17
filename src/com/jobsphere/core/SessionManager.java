package com.jobsphere.core;

/**
 * Manages user session (login/logout state).
 * Single Responsibility: Only handles authentication session.
 */
public class SessionManager {
    private User currentUser;

    public User login(UserRepository userRepo, String username, String password) {
        User user = userRepo.findByUsername(username);
        if (user != null && user.checkPassword(password)) {
            this.currentUser = user;
            return currentUser;
        }
        return null;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
