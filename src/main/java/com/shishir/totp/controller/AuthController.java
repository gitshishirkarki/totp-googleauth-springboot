package com.shishir.totp.controller;

import com.shishir.totp.domain.User;
import com.shishir.totp.dto.LoginRequest;
import com.shishir.totp.dto.MfaVerificationResponse;
import com.shishir.totp.dto.RegisterRequest;
import com.shishir.totp.dto.VerifyTotpRequest;
import com.shishir.totp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            User user = userService.register(registerRequest.getUsername(), registerRequest.getPassword());
            user.setPassword(null);
            user.setMfaSecret(null);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Something went wrong");
        }
    }

    @GetMapping("qr/generate")
    public ResponseEntity<?> generateQR(@RequestParam String username) {
        return ResponseEntity.ok(userService.generateTotpQR(username));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        if (user != null) {
            return ResponseEntity.ok(MfaVerificationResponse.builder()
                    .username(loginRequest.getUsername())
                    .tokenValid(Boolean.FALSE)
                    .authValid(Boolean.TRUE)
                    .mfaRequired(Boolean.TRUE)
                    .message("User authenticated using username and password")
                    .build());
        }

        return ResponseEntity.ok(MfaVerificationResponse.builder()
                .username(loginRequest.getUsername())
                .tokenValid(Boolean.FALSE)
                .authValid(Boolean.FALSE)
                .mfaRequired(Boolean.FALSE)
                .message("Invalid credentials. Please try again.")
                .build());
    }

    @PostMapping("/verifyTotp")
    public ResponseEntity<?> verifyTwoFactor(@RequestBody VerifyTotpRequest verifyTotpRequest) {
        MfaVerificationResponse mfaVerificationResponse = MfaVerificationResponse.builder()
                .username(verifyTotpRequest.getUsername())
                .tokenValid(Boolean.FALSE)
                .message("Token is not valid. Please try again.")
                .build();

        if (userService.verifyTotp(verifyTotpRequest.getUsername(), verifyTotpRequest.getCode())) {
            mfaVerificationResponse = MfaVerificationResponse.builder()
                    .username(verifyTotpRequest.getUsername())
                    .tokenValid(Boolean.TRUE)
                    .jwt("DUMMYJWT")
                    .message("Token is valid.")
                    .build();
        }

        return ResponseEntity.ok(mfaVerificationResponse);
    }


}

