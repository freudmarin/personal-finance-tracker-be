package com.marin.dulja.personalfinancetrackerbe.category;

import com.marin.dulja.personalfinancetrackerbe.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findAllByUserOrderByNameAsc(User user);
    Optional<Category> findByIdAndUser(UUID id, User user);
    boolean existsByIdAndUser(UUID id, User user);
    long deleteByIdAndUser(UUID id, User user);
    boolean existsByUserAndName(User user, String name);
}
