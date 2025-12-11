package com.marin.dulja.personalfinancetrackerbe.security.token;
import java.util.UUID;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);

    @Query("SELECT rt FROM RefreshToken rt JOIN FETCH rt.user WHERE rt.token = :token")
    Optional<RefreshToken> findByTokenWithUser(String token);
}


