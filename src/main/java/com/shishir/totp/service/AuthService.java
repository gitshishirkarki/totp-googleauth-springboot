package com.shishir.totp.service;

import com.shishir.totp.domain.User;
import com.shishir.totp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User register(String username, String password, boolean twoFactorEnabled, String twoFactorSecret) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setTwoFactorEnabled(twoFactorEnabled);
        user.setTwoFactorSecret(twoFactorSecret);
        return userRepository.save(user);
    }

    public User login(String username, String password) {
        User user = this.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    public boolean verifyTwoFactorCode(User user, String code) {
        // In a real implementation, use a library like Google Authenticator to verify the 2FA code
        // This is a placeholder for demo purposes
        return true;
    }

    public User findByUsername(String username){
        return userRepository.findByUsername(username);
    }
}
