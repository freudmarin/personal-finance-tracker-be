package com.marin.dulja.personalfinancetrackerbe.security.auth;

import com.marin.dulja.personalfinancetrackerbe.security.CustomUserDetails;
import com.marin.dulja.personalfinancetrackerbe.security.JwtProperties;
import com.marin.dulja.personalfinancetrackerbe.security.JwtService;
import com.marin.dulja.personalfinancetrackerbe.security.token.RefreshToken;
import com.marin.dulja.personalfinancetrackerbe.security.token.RefreshTokenService;
import com.marin.dulja.personalfinancetrackerbe.user.User;
import com.marin.dulja.personalfinancetrackerbe.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
    public AuthResponse login(@Valid LoginRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        var accessToken = jwtService.generateAccessToken(userDetails.getUser());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUser());
        return new AuthResponse(accessToken, refreshToken.getToken(), jwtProperties.getAccessTokenExpirationMs());
    }

    @Transactional
    public AuthResponse register(@Valid RegisterRequest request) {
        try {
            if (userRepository.existsByEmail(request.getEmail()) || userRepository.existsByUsername(request.getUsername())) {
                // Always return generic error, do not reveal if email or username exists
                throw new IllegalArgumentException();
            }
            User user = new User();
            user.setEmail(request.getEmail());
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.getRoles().add("ROLE_USER");
            userRepository.save(user);
            // Authenticate and generate tokens for the new user
            String accessToken = jwtService.generateAccessToken(user);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
            return new AuthResponse(accessToken, refreshToken.getToken(), jwtProperties.getAccessTokenExpirationMs());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Registration failed");
        }
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
