package com.collabera.library.api.dto;

import com.collabera.library.domain.Book;
import java.time.Instant;

public record BookResponse(Long id, String isbn, String title, String author, boolean available, Instant createdAt) {
    public static BookResponse from(Book book, boolean available) {
        return new BookResponse(book.getId(), book.getCatalog().getIsbn(), book.getCatalog().getTitle(),
                book.getCatalog().getAuthor(), available, book.getCreatedAt());
    }
}
