package com.shishir.totp.dto;

public class VerifyTotpRequest {
    private String username;
    private int code;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
