package com.marin.dulja.personalfinancetrackerbe.transaction;

import com.marin.dulja.personalfinancetrackerbe.category.Category;
import com.marin.dulja.personalfinancetrackerbe.category.CategoryNotFoundException;
import com.marin.dulja.personalfinancetrackerbe.category.CategoryRepository;
import com.marin.dulja.personalfinancetrackerbe.category.dto.CategoryResponse;
import com.marin.dulja.personalfinancetrackerbe.transaction.dto.TransactionRequest;
import com.marin.dulja.personalfinancetrackerbe.transaction.dto.TransactionResponse;
import com.marin.dulja.personalfinancetrackerbe.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

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

    public List<TransactionResponse> list(UUID userId) {
        log.debug("Listing all transactions for userId: {}", userId);
        List<TransactionResponse> result = repository.findAllByUser_IdOrderByDateDesc(userId)
                .stream()
                .map(TransactionService::toResponse)
                .toList();
        log.debug("Found {} transactions for userId: {}", result.size(), userId);
        return result;
    }

    public List<TransactionResponse> list(UUID userId, String type, UUID categoryId) {
        log.debug("Listing transactions for userId: {}, type: {}, categoryId: {}", userId, type, categoryId);
        List<Transaction> items;
        if (type != null && !type.isBlank()) {
            if (categoryId != null) {
                items = repository.findAllByUser_IdAndTypeAndCategoryRef_IdOrderByDateDesc(userId, type, categoryId);
            } else {
                items = repository.findAllByUser_IdAndTypeOrderByDateDesc(userId, type);
            }
        } else {
            items = (categoryId != null)
                    ? repository.findAllByUser_IdAndCategoryRef_IdOrderByDateDesc(userId, categoryId)
                    : repository.findAllByUser_IdOrderByDateDesc(userId);
        }
        List<TransactionResponse> result = items.stream().map(TransactionService::toResponse).toList();
        log.debug("Found {} transactions for userId: {}", result.size(), userId);
        return result;
    }

    public TransactionResponse getOne(UUID id, UUID userId) {
        Transaction e = repository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new TransactionNotFoundException(id));
        return toResponse(e);
    }

    @Transactional
    public TransactionResponse create(TransactionRequest req, UUID userId) {
        Transaction e = new Transaction();
        User user = new User();
        user.setId(userId);
        e.setUser(user);
        e.setTitle(req.title());
        e.setAmount(req.amount());
        e.setDate(req.date());
        e.setType(req.type());
        if (req.categoryId() != null) {
            Category cat = categoryRepository.findByIdAndUser_Id(req.categoryId(), userId)
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
    public TransactionResponse update(UUID id, TransactionRequest req, UUID userId) {
        Transaction e = repository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new TransactionNotFoundException(id));
        e.setTitle(req.title());
        e.setAmount(req.amount());
        e.setDate(req.date());
        e.setType(req.type());
        if (req.categoryId() != null) {
            Category cat = categoryRepository.findByIdAndUser_Id(req.categoryId(), userId)
                    .orElseThrow(() -> new CategoryNotFoundException(req.categoryId()));
            e.setCategoryRef(cat);
        } else {
            e.setCategoryRef(null);
        }
        e.setTags(joinTags(req.tags()));
        return toResponse(repository.save(e));
    }

    @Transactional
    public void delete(UUID id, UUID userId) {
        boolean exists = repository.existsByIdAndUser_Id(id, userId);
        if (!exists) {
            throw new TransactionNotFoundException(id);
        }
        repository.deleteByIdAndUser_Id(id, userId);
    }
}
