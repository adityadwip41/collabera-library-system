package com.collabera.library.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "book_catalog")
public class BookCatalog {
    @Id @Column(length = 17)
    private String isbn;
    @Column(nullable = false, length = 255)
    private String title;
    @Column(nullable = false, length = 255)
    private String author;

    protected BookCatalog() {}
    public BookCatalog(String isbn, String title, String author) {
        this.isbn = isbn; this.title = title; this.author = author;
    }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public boolean hasMetadata(String title, String author) {
        return this.title.equals(title) && this.author.equals(author);
    }
}
