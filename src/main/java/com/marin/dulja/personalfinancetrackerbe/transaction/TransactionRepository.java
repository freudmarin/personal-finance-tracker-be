package com.marin.dulja.personalfinancetrackerbe.transaction;

import com.marin.dulja.personalfinancetrackerbe.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findAllByUserOrderByDateDesc(User user);

    List<Transaction> findAllByUserAndCategoryRef_IdOrderByDateDesc(User user, UUID categoryId);

    Optional<Transaction> findByIdAndUser(UUID id, User user);

    boolean existsByIdAndUser(UUID id, User user);

    long deleteByIdAndUser(UUID id, User user);

    long deleteByUserAndCategoryRef_Id(User user, UUID categoryId);

    List<Transaction> findAllByUserAndTypeOrderByDateDesc(User user, String type);

    List<Transaction> findAllByUserAndTypeAndCategoryRef_IdOrderByDateDesc(User user, String type, UUID categoryId);
}
