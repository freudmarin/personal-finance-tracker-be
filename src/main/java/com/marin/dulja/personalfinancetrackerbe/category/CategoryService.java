package com.marin.dulja.personalfinancetrackerbe.category;

import com.marin.dulja.personalfinancetrackerbe.category.dto.CategoryRequest;
import com.marin.dulja.personalfinancetrackerbe.category.dto.CategoryResponse;
import com.marin.dulja.personalfinancetrackerbe.transaction.TransactionRepository;
import com.marin.dulja.personalfinancetrackerbe.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository repository;
    private final TransactionRepository transactionRepository;

    public CategoryService(CategoryRepository repository, TransactionRepository transactionRepository) {
        this.repository = repository;
        this.transactionRepository = transactionRepository;
    }

    public List<CategoryResponse> list(UUID userId) {
        return repository.findAllByUser_IdOrderByNameAsc(userId)
                .stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName()))
                .toList();
    }

    @Transactional
    public CategoryResponse create(CategoryRequest req, UUID userId) {
        if (repository.existsByUser_IdAndName(userId, req.name())) {
            throw new DuplicateCategoryException(userId.toString(), req.name());
        }
        Category c = new Category();
        User user = new User();
        user.setId(userId);
        c.setUser(user);
        c.setName(req.name());
        Category saved = repository.save(c);
        return new CategoryResponse(saved.getId(), saved.getName());
    }

    @Transactional
    public CategoryResponse update(UUID id, CategoryRequest req, UUID userId) {
        Category c = repository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        boolean duplicate = repository.existsByUser_IdAndName(userId, req.name()) && !c.getName().equals(req.name());
        if (duplicate) {
            throw new DuplicateCategoryException(userId.toString(), req.name());
        }
        c.setName(req.name());
        Category saved = repository.save(c);
        return new CategoryResponse(saved.getId(), saved.getName());
    }

    @Transactional
    public void delete(UUID id, UUID userId) {
        boolean exists = repository.existsByIdAndUser_Id(id, userId);
        if (!exists) throw new CategoryNotFoundException(id);
        transactionRepository.deleteByUser_IdAndCategoryRef_Id(userId, id);
        repository.deleteByIdAndUser_Id(id, userId);
    }
}
