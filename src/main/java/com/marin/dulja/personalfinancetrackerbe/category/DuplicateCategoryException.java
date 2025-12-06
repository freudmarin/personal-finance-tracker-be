package com.marin.dulja.personalfinancetrackerbe.category;

public class DuplicateCategoryException extends RuntimeException {
    public DuplicateCategoryException(String clientId, String name) {
        super("Category already exists for client: " + clientId + ", name: " + name);
    }
}

