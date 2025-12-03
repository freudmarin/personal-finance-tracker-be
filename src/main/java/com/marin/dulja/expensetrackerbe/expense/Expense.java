package com.marin.dulja.expensetrackerbe.expense;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "expenses", indexes = {
        @Index(name = "idx_expenses_client_id", columnList = "client_id")
})
public class Expense {

    @Id
    @GeneratedValue
    @org.hibernate.annotations.UuidGenerator
    @Column(nullable = false)
    private UUID id;

    @Column(name = "client_id", nullable = false, length = 100)
    private String clientId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate date;

    public Expense() {
    }

    public Expense(UUID id, String clientId, String title, BigDecimal amount, LocalDate date) {
        this.id = id;
        this.clientId = clientId;
        this.title = title;
        this.amount = amount;
        this.date = date;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
