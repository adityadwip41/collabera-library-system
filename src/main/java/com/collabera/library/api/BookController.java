package com.collabera.library.api;

import com.collabera.library.api.dto.*;
import com.collabera.library.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {
    private final BookService service;
    public BookController(BookService service) { this.service = service; }

    @PostMapping
    @Operation(summary = "Register one physical copy of a book")
    public ResponseEntity<BookResponse> register(@Valid @RequestBody RegisterBookRequest request) {
        BookResponse response = service.register(request);
        return ResponseEntity.created(URI.create("/api/v1/books/" + response.id())).body(response);
    }

    @GetMapping
    @Operation(summary = "List all registered book copies")
    public PageResponse<BookResponse> list(
            @RequestParam(defaultValue = "0") @jakarta.validation.constraints.Min(0) int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        return service.list(page, size, sortBy, direction);
    }
}
