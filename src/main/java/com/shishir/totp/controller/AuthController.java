package com.shishir.totp.controller;

import com.shishir.totp.domain.User;
import com.shishir.totp.googleauthentication.GAService;
import com.shishir.totp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private GAService gaService;

    @PostMapping("/register")
    public User register(@RequestParam String username,
                         @RequestParam String password,
                         @RequestParam boolean twoFactorEnabled,
                         @RequestParam(required = false) String twoFactorSecret) {
        return authService.register(username, password, twoFactorEnabled, twoFactorSecret);
    }

    @PostMapping("/login")
    public User login(@RequestParam String username,
                      @RequestParam String password) {
        return authService.login(username, password);
    }

    @PostMapping("/verify")
    public boolean verifyTwoFactor(@RequestParam String username,
                                   @RequestParam int code) {
        User user = authService.findByUsername(username);
        return user != null && gaService.isValid(user.getTwoFactorSecret(), code);
    }

    @GetMapping("qr/generate")
    public String generateQR(@RequestParam String username) {
        return authService.generateQR(username);
    }
}

