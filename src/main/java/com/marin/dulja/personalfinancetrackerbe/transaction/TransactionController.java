package com.marin.dulja.personalfinancetrackerbe.transaction;

import com.marin.dulja.personalfinancetrackerbe.security.CustomUserDetails;
import com.marin.dulja.personalfinancetrackerbe.transaction.dto.TransactionRequest;
import com.marin.dulja.personalfinancetrackerbe.transaction.dto.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@PreAuthorize("isAuthenticated()")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<TransactionResponse> list(@AuthenticationPrincipal CustomUserDetails userDetails,
                                      @RequestParam(value = "categoryId", required = false) UUID categoryId,
                                      @RequestParam(value = "type", required = false) String type) {
        return service.list(userDetails.getUserId(), type, categoryId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public TransactionResponse getOne(@PathVariable UUID id,
                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        return service.getOne(id, userDetails.getUserId());
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody TransactionRequest req,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        TransactionResponse saved = service.create(req, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public TransactionResponse update(@PathVariable UUID id,
                                      @Valid @RequestBody TransactionRequest req,
                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        return service.update(id, req, userDetails.getUserId());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(@PathVariable UUID id,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        service.delete(id, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }
}
