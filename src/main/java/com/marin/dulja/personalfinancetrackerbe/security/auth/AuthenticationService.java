package com.marin.dulja.personalfinancetrackerbe.security.auth;

import com.marin.dulja.personalfinancetrackerbe.security.CustomUserDetails;
import com.marin.dulja.personalfinancetrackerbe.security.JwtProperties;
import com.marin.dulja.personalfinancetrackerbe.security.JwtService;
import com.marin.dulja.personalfinancetrackerbe.security.token.RefreshToken;
import com.marin.dulja.personalfinancetrackerbe.security.token.RefreshTokenService;
import com.marin.dulja.personalfinancetrackerbe.user.User;
import com.marin.dulja.personalfinancetrackerbe.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(AuthenticationManager authenticationManager,
                                 JwtService jwtService,
                                 RefreshTokenService refreshTokenService,
                                 JwtProperties jwtProperties,
                                 UserRepository userRepository,
                                 PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.jwtProperties = jwtProperties;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResponse login(@Valid AuthRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        var accessToken = jwtService.generateAccessToken(userDetails.getUser());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUser());
        return new AuthResponse(accessToken, refreshToken.getToken(), jwtProperties.getAccessTokenExpirationMs());
    }

    @Transactional
    public void register(@Valid AuthRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already in use");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.getRoles().add("ROLE_USER");
        userRepository.save(user);
    }

    public AccessTokenResponse refresh(@Valid RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.verifyToken(request.getRefreshToken());
        String accessToken = jwtService.generateAccessToken(refreshToken.getUser());
        return new AccessTokenResponse(accessToken);
    }

    public void logout(@Valid RefreshTokenRequest request) {
        refreshTokenService.revoke(request.getRefreshToken());
    }
}

