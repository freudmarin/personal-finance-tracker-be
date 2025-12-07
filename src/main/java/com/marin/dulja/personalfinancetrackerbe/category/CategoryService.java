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

    public List<CategoryResponse> list(User user) {
        return repository.findAllByUserOrderByNameAsc(user)
                .stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName()))
                .toList();
    }

    @Transactional
    public CategoryResponse create(CategoryRequest req, User user) {
        if (repository.existsByUserAndName(user, req.name())) {
            throw new DuplicateCategoryException(user.getId().toString(), req.name());
        }
        Category c = new Category();
        c.setUser(user);
        c.setName(req.name());
        Category saved = repository.save(c);
        return new CategoryResponse(saved.getId(), saved.getName());
    }

    @Transactional
    public CategoryResponse update(UUID id, CategoryRequest req, User user) {
        Category c = repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        boolean duplicate = repository.existsByUserAndName(user, req.name()) && !c.getName().equals(req.name());
        if (duplicate) {
            throw new DuplicateCategoryException(user.getId().toString(), req.name());
        }
        c.setName(req.name());
        Category saved = repository.save(c);
        return new CategoryResponse(saved.getId(), saved.getName());
    }

    @Transactional
    public void delete(UUID id, User user) {
        boolean exists = repository.existsByIdAndUser(id, user);
        if (!exists) throw new CategoryNotFoundException(id);
        transactionRepository.deleteByUserAndCategoryRef_Id(user, id);
        repository.deleteByIdAndUser(id, user);
    }
}
