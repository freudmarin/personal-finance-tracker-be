package com.marin.dulja.personalfinancetrackerbe.transaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findAllByUser_IdOrderByDateDesc(UUID userId);

    List<Transaction> findAllByUser_IdAndCategoryRef_IdOrderByDateDesc(UUID userId, UUID categoryId);

    Optional<Transaction> findByIdAndUser_Id(UUID id, UUID userId);

    boolean existsByIdAndUser_Id(UUID id, UUID userId);

    long deleteByIdAndUser_Id(UUID id, UUID userId);

    long deleteByUser_IdAndCategoryRef_Id(UUID userId, UUID categoryId);

    List<Transaction> findAllByUser_IdAndTypeOrderByDateDesc(UUID userId, String type);

    List<Transaction> findAllByUser_IdAndTypeAndCategoryRef_IdOrderByDateDesc(UUID userId, String type, UUID categoryId);
}
