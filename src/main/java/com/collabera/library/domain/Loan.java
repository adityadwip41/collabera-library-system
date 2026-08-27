package com.collabera.library.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "loans", uniqueConstraints = @UniqueConstraint(name = "uk_active_loan_book", columnNames = "book_id"))
public class Loan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "borrower_id", nullable = false)
    private Borrower borrower;
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    @Column(name = "borrowed_at", nullable = false, updatable = false)
    private Instant borrowedAt = Instant.now();

    protected Loan() {}
    public Loan(Borrower borrower, Book book) { this.borrower = borrower; this.book = book; }
    public Long getId() { return id; }
    public Borrower getBorrower() { return borrower; }
    public Book getBook() { return book; }
    public Instant getBorrowedAt() { return borrowedAt; }
}
