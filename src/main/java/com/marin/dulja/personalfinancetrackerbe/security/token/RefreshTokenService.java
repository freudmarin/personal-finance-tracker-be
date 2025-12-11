package com.marin.dulja.personalfinancetrackerbe.security.token;

import com.marin.dulja.personalfinancetrackerbe.security.JwtProperties;
import com.marin.dulja.personalfinancetrackerbe.user.User;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtProperties jwtProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProperties = jwtProperties;
    }

    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(jwtProperties.getRefreshTokenExpirationMs()));
        refreshToken.setRevoked(false);
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenWithUser(token)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));
        if (refreshToken.isRevoked() || refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new BadCredentialsException("Refresh token is expired or revoked");
        }
        return refreshToken;
    }

    public void revoke(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
    }
}

