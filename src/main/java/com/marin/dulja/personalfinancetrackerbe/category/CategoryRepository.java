package com.marin.dulja.personalfinancetrackerbe.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    @Query("SELECT c FROM Category c WHERE c.user.id = :userId ORDER BY c.name ASC")
    List<Category> findAllByUser_IdOrderByNameAsc(@Param("userId") UUID userId);

    @Query("SELECT c FROM Category c WHERE c.id = :id AND c.user.id = :userId")
    Optional<Category> findByIdAndUser_Id(@Param("id") UUID id, @Param("userId") UUID userId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Category c WHERE c.id = :id AND c.user.id = :userId")
    boolean existsByIdAndUser_Id(@Param("id") UUID id, @Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM Category c WHERE c.id = :id AND c.user.id = :userId")
    long deleteByIdAndUser_Id(@Param("id") UUID id, @Param("userId") UUID userId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Category c WHERE c.user.id = :userId AND c.name = :name")
    boolean existsByUser_IdAndName(@Param("userId") UUID userId, @Param("name") String name);
}
