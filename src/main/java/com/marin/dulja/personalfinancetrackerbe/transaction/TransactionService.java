package com.marin.dulja.personalfinancetrackerbe.transaction;

import com.marin.dulja.personalfinancetrackerbe.category.Category;
import com.marin.dulja.personalfinancetrackerbe.category.CategoryNotFoundException;
import com.marin.dulja.personalfinancetrackerbe.category.CategoryRepository;
import com.marin.dulja.personalfinancetrackerbe.category.dto.CategoryResponse;
import com.marin.dulja.personalfinancetrackerbe.transaction.dto.TransactionRequest;
import com.marin.dulja.personalfinancetrackerbe.transaction.dto.TransactionResponse;
import com.marin.dulja.personalfinancetrackerbe.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionRepository repository;
    private final CategoryRepository categoryRepository;

    public TransactionService(TransactionRepository repository, CategoryRepository categoryRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
    }

    private static TransactionResponse toResponse(Transaction e) {
        List<String> tags = parseTags(e.getTags());
        CategoryResponse categoryResponse = e.getCategoryRef() != null
                ? new CategoryResponse(e.getCategoryRef().getId(), e.getCategoryRef().getName())
                : null;
        return new TransactionResponse(e.getId(), e.getTitle(), e.getAmount(), e.getDate(), e.getType(), categoryResponse, tags);
    }

    private static List<String> parseTags(String tagsStr) {
        if (tagsStr == null || tagsStr.isBlank()) return List.of();
        return Arrays.stream(tagsStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private static String joinTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) return null;
        List<String> cleaned = new ArrayList<>();
        for (String t : tags) {
            if (t == null) continue;
            String c = t.trim();
            if (!c.isEmpty()) cleaned.add(c);
        }
        if (cleaned.isEmpty()) return null;
        return String.join(",", cleaned);
    }

    public List<TransactionResponse> list(User user) {
        return repository.findAllByUserOrderByDateDesc(user)
                .stream()
                .map(TransactionService::toResponse)
                .toList();
    }

    public List<TransactionResponse> list(User user, String type, UUID categoryId) {
        List<Transaction> items;
        if (type != null && !type.isBlank()) {
            if (categoryId != null) {
                items = repository.findAllByUserAndTypeAndCategoryRef_IdOrderByDateDesc(user, type, categoryId);
            } else {
                items = repository.findAllByUserAndTypeOrderByDateDesc(user, type);
            }
        } else {
            items = (categoryId != null)
                    ? repository.findAllByUserAndCategoryRef_IdOrderByDateDesc(user, categoryId)
                    : repository.findAllByUserOrderByDateDesc(user);
        }
        return items.stream().map(TransactionService::toResponse).toList();
    }

    public TransactionResponse getOne(UUID id, User user) {
        Transaction e = repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new TransactionNotFoundException(id));
        return toResponse(e);
    }

    @Transactional
    public TransactionResponse create(TransactionRequest req, User user) {
        Transaction e = new Transaction();
        e.setUser(user);
        e.setTitle(req.title());
        e.setAmount(req.amount());
        e.setDate(req.date());
        e.setType(req.type());
        if (req.categoryId() != null) {
            Category cat = categoryRepository.findByIdAndUser(req.categoryId(), user)
                    .orElseThrow(() -> new CategoryNotFoundException(req.categoryId()));
            e.setCategoryRef(cat);
        } else {
            e.setCategoryRef(null);
        }
        e.setTags(joinTags(req.tags()));
        Transaction saved = repository.save(e);
        return toResponse(saved);
    }

    @Transactional
    public TransactionResponse update(UUID id, TransactionRequest req, User user) {
        Transaction e = repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new TransactionNotFoundException(id));
        e.setTitle(req.title());
        e.setAmount(req.amount());
        e.setDate(req.date());
        e.setType(req.type());
        if (req.categoryId() != null) {
            Category cat = categoryRepository.findByIdAndUser(req.categoryId(), user)
                    .orElseThrow(() -> new CategoryNotFoundException(req.categoryId()));
            e.setCategoryRef(cat);
        } else {
            e.setCategoryRef(null);
        }
        e.setTags(joinTags(req.tags()));
        return toResponse(repository.save(e));
    }

    @Transactional
    public void delete(UUID id, User user) {
        boolean exists = repository.existsByIdAndUser(id, user);
        if (!exists) {
            throw new TransactionNotFoundException(id);
        }
        repository.deleteByIdAndUser(id, user);
    }
}
