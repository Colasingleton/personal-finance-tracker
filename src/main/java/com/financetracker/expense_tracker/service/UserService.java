package com.financetracker.expense_tracker.service;

import com.financetracker.expense_tracker.entity.User;
import com.financetracker.expense_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);

    }
        public User registerNewUser(String username, String password, String email) {
        if(userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exist");
        }
        if(userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exist");
        }

        User user = new User(username, passwordEncoder.encode(password), email);
        return userRepository.save(user);
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}
