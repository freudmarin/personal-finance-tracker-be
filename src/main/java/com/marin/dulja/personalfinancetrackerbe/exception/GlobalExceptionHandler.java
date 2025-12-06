package com.marin.dulja.personalfinancetrackerbe.exception;

import com.marin.dulja.personalfinancetrackerbe.category.DuplicateCategoryException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DuplicateCategoryException.class)
    public ResponseEntity<String> handleDuplicate(DuplicateCategoryException ex) {
        return ResponseEntity.status(409).body(ex.getMessage());
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity.status(409).body("Conflict: duplicate resource or constraint violation");
    }
}

