package com.marin.dulja.personalfinancetrackerbe.category;

import com.marin.dulja.personalfinancetrackerbe.category.dto.CategoryRequest;
import com.marin.dulja.personalfinancetrackerbe.category.dto.CategoryResponse;
import com.marin.dulja.personalfinancetrackerbe.transaction.TransactionRepository;
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

    public List<CategoryResponse> list(String clientId) {
        return repository.findAllByClientIdOrderByNameAsc(clientId)
                .stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName()))
                .toList();
    }

    @Transactional
    public CategoryResponse create(CategoryRequest req, String clientId) {
        if (repository.existsByClientIdAndName(clientId, req.name())) {
            throw new DuplicateCategoryException(clientId, req.name());
        }
        Category c = new Category();
        c.setClientId(clientId);
        c.setName(req.name());
        Category saved = repository.save(c);
        return new CategoryResponse(saved.getId(), saved.getName());
    }

    @Transactional
    public CategoryResponse update(UUID id, CategoryRequest req, String clientId) {
        Category c = repository.findByIdAndClientId(id, clientId)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        // Check for duplicate name (excluding current category)
        boolean duplicate = repository.existsByClientIdAndName(clientId, req.name()) && !c.getName().equals(req.name());
        if (duplicate) {
            throw new DuplicateCategoryException(clientId, req.name());
        }
        c.setName(req.name());
        Category saved = repository.save(c);
        return new CategoryResponse(saved.getId(), saved.getName());
    }

    @Transactional
    public void delete(UUID id, String clientId) {
        boolean exists = repository.existsByIdAndClientId(id, clientId);
        if (!exists) throw new CategoryNotFoundException(id);
        // First delete all transactions that belong to this client and reference the category
        transactionRepository.deleteByClientIdAndCategoryRef_Id(clientId, id);
        // Then delete the category itself
        repository.deleteByIdAndClientId(id, clientId);
    }
}
