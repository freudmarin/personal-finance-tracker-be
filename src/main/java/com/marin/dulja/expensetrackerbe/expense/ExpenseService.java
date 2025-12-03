package com.marin.dulja.expensetrackerbe.expense;

import com.marin.dulja.expensetrackerbe.expense.dto.ExpenseRequest;
import com.marin.dulja.expensetrackerbe.expense.dto.ExpenseResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ExpenseService {

    private final ExpenseRepository repository;

    public ExpenseService(ExpenseRepository repository) {
        this.repository = repository;
    }

    private static ExpenseResponse toResponse(Expense e) {
        return new ExpenseResponse(e.getId(), e.getTitle(), e.getAmount(), e.getDate());
    }

    public List<ExpenseResponse> list(String clientId) {
        return repository.findAllByClientIdOrderByDateDesc(clientId)
                .stream()
                .map(ExpenseService::toResponse)
                .toList();
    }

    public ExpenseResponse getOne(UUID id, String clientId) {
        Expense e = repository.findByIdAndClientId(id, clientId)
                .orElseThrow(() -> new ExpenseNotFoundException(id));
        return toResponse(e);
    }

    @Transactional
    public ExpenseResponse create(ExpenseRequest req, String clientId) {
        Expense e = new Expense();
        e.setClientId(clientId);
        e.setTitle(req.title());
        e.setAmount(req.amount());
        e.setDate(req.date());
        Expense saved = repository.save(e);
        return toResponse(saved);
    }

    @Transactional
    public ExpenseResponse update(UUID id, ExpenseRequest req, String clientId) {
        Expense e = repository.findByIdAndClientId(id, clientId)
                .orElseThrow(() -> new ExpenseNotFoundException(id));
        e.setTitle(req.title());
        e.setAmount(req.amount());
        e.setDate(req.date());
        return toResponse(repository.save(e));
    }

    @Transactional
    public void delete(UUID id, String clientId) {
        boolean exists = repository.existsByIdAndClientId(id, clientId);
        if (!exists) {
            throw new ExpenseNotFoundException(id);
        }
        repository.deleteByIdAndClientId(id, clientId);
    }
}
