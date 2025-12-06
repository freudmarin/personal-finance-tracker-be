package com.marin.dulja.personalfinancetrackerbe.error;

import com.marin.dulja.personalfinancetrackerbe.category.CategoryNotFoundException;
import com.marin.dulja.personalfinancetrackerbe.category.DuplicateCategoryException;
import com.marin.dulja.personalfinancetrackerbe.transaction.TransactionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(TransactionNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "not_found");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(CategoryNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "not_found");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(DuplicateCategoryException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate(DuplicateCategoryException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "conflict");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "validation_error");
        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        body.put("fields", errors);
        return ResponseEntity.badRequest().body(body);
    }
}
