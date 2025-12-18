package com.jobsphere.core;


public class RealLoginService implements LoginService {

    private final UserRepository userRepository;

    public RealLoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.checkPassword(password)) {
            return user;
        }
        return null;
    }
}

