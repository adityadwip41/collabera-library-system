package com.collabera.library.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "books")
public class Book {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "isbn", nullable = false)
    private BookCatalog catalog;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Book() {}
    public Book(BookCatalog catalog) { this.catalog = catalog; }
    public Long getId() { return id; }
    public BookCatalog getCatalog() { return catalog; }
    public Instant getCreatedAt() { return createdAt; }
}
