package com.marin.dulja.personalfinancetrackerbe.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId ORDER BY t.date DESC")
    List<Transaction> findAllByUser_IdOrderByDateDesc(@Param("userId") UUID userId);

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.categoryRef.id = :categoryId ORDER BY t.date DESC")
    List<Transaction> findAllByUser_IdAndCategoryRef_IdOrderByDateDesc(@Param("userId") UUID userId, @Param("categoryId") UUID categoryId);

    @Query("SELECT t FROM Transaction t WHERE t.id = :id AND t.user.id = :userId")
    Optional<Transaction> findByIdAndUser_Id(@Param("id") UUID id, @Param("userId") UUID userId);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Transaction t WHERE t.id = :id AND t.user.id = :userId")
    boolean existsByIdAndUser_Id(@Param("id") UUID id, @Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM Transaction t WHERE t.id = :id AND t.user.id = :userId")
    long deleteByIdAndUser_Id(@Param("id") UUID id, @Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM Transaction t WHERE t.user.id = :userId AND t.categoryRef.id = :categoryId")
    long deleteByUser_IdAndCategoryRef_Id(@Param("userId") UUID userId, @Param("categoryId") UUID categoryId);

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.type = :type ORDER BY t.date DESC")
    List<Transaction> findAllByUser_IdAndTypeOrderByDateDesc(@Param("userId") UUID userId, @Param("type") String type);

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.type = :type AND t.categoryRef.id = :categoryId ORDER BY t.date DESC")
    List<Transaction> findAllByUser_IdAndTypeAndCategoryRef_IdOrderByDateDesc(@Param("userId") UUID userId, @Param("type") String type, @Param("categoryId") UUID categoryId);
}
