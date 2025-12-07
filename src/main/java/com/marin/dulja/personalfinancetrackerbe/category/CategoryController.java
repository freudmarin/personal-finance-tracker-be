package com.marin.dulja.personalfinancetrackerbe.category;

import com.marin.dulja.personalfinancetrackerbe.category.dto.CategoryRequest;
import com.marin.dulja.personalfinancetrackerbe.category.dto.CategoryResponse;
import com.marin.dulja.personalfinancetrackerbe.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@PreAuthorize("isAuthenticated()")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<CategoryResponse> list(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return service.list(userDetails.getUser());
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest req,
                                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        CategoryResponse saved = service.create(req, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public CategoryResponse update(@PathVariable UUID id,
                                   @Valid @RequestBody CategoryRequest req,
                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        return service.update(id, req, userDetails.getUser());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(@PathVariable UUID id,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        service.delete(id, userDetails.getUser());
        return ResponseEntity.noContent().build();
    }
}
