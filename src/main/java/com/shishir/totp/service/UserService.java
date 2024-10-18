package com.shishir.totp.service;

import com.shishir.totp.domain.User;
import com.shishir.totp.googleauthentication.GAService;
import com.shishir.totp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private GAService gaService;

    public User register(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setMfaEnabled(Boolean.TRUE);
        user.setMfaSecret(gaService.generateKey());
        return userRepository.save(user);
    }

    public User login(String username, String password) {
        User user = this.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    public boolean verifyTotp(String username, int code) {
        User user = this.findByUsername(username);
        return user != null && gaService.isValid(user.getMfaSecret(), code);
    }

    public String generateTotpQR(String username) {
        User user = this.findByUsername(username);
        if (user != null) {
            return gaService.generateQRUrl(user.getMfaSecret(), username);
        }

        return "";
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
