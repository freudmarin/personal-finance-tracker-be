package com.marin.dulja.personalfinancetrackerbe.security.auth;

public class AuthResponse {

    private String username;
    private String accessToken;
    private String refreshToken;
    private long expiresIn;

    public AuthResponse(String username, String accessToken, String refreshToken, long expiresIn) {
        this.username = username;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    public String getUsername() {
        return username;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }
}

