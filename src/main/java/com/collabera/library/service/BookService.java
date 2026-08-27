package com.collabera.library.service;

import com.collabera.library.api.dto.*;
import com.collabera.library.domain.*;
import com.collabera.library.exception.ConflictException;
import com.collabera.library.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {
    private final BookCatalogRepository catalogs;
    private final BookRepository books;
    private final LoanRepository loans;

    public BookService(BookCatalogRepository catalogs, BookRepository books, LoanRepository loans) {
        this.catalogs = catalogs; this.books = books; this.loans = loans;
    }

    @Transactional
    public BookResponse register(RegisterBookRequest request) {
        String isbn = request.isbn().trim();
        String title = request.title().trim();
        String author = request.author().trim();
        BookCatalog catalog = catalogs.findById(isbn)
                .orElseGet(() -> catalogs.save(new BookCatalog(isbn, title, author)));
        if (!catalog.hasMetadata(title, author)) {
            throw new ConflictException("This ISBN is already registered with a different title or author");
        }
        return BookResponse.from(books.save(new Book(catalog)), true);
    }

    @Transactional(readOnly = true)
    public PageResponse<BookResponse> list(int page, int size, String sortBy, Sort.Direction direction) {
        if (size < 1 || size > 100) throw new IllegalArgumentException("size must be between 1 and 100");
        String property = switch (sortBy) {
            case "id", "createdAt" -> sortBy;
            case "isbn", "title", "author" -> "catalog." + sortBy;
            default -> throw new IllegalArgumentException("Unsupported sort field: " + sortBy);
        };
        Page<Book> result = books.findAll(PageRequest.of(page, size, Sort.by(direction, property)));
        var bookIds = result.getContent().stream().map(Book::getId).toList();
        var borrowedBookIds = bookIds.isEmpty()
                ? java.util.Set.<Long>of()
                : new java.util.HashSet<>(loans.findBorrowedBookIds(bookIds));
        return PageResponse.from(result, book -> BookResponse.from(book, !borrowedBookIds.contains(book.getId())));
    }
}
