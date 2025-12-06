package com.marin.dulja.personalfinancetrackerbe.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findAllByClientIdOrderByNameAsc(String clientId);
    Optional<Category> findByIdAndClientId(UUID id, String clientId);
    boolean existsByIdAndClientId(UUID id, String clientId);
    long deleteByIdAndClientId(UUID id, String clientId);
    boolean existsByClientIdAndName(String clientId, String name);
}
