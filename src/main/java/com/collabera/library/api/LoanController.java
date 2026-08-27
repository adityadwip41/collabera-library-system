package com.collabera.library.api;

import com.collabera.library.api.dto.LoanResponse;
import com.collabera.library.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/borrowers/{borrowerId}/borrowed-books/{bookId}")
public class LoanController {
    private final LoanService service;
    public LoanController(LoanService service) { this.service = service; }

    @PostMapping
    @Operation(summary = "Borrow a specific book copy")
    public ResponseEntity<LoanResponse> borrow(@PathVariable long borrowerId, @PathVariable long bookId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.borrow(borrowerId, bookId));
    }

    @DeleteMapping
    @Operation(summary = "Return a specific book copy")
    public LoanResponse returnBook(@PathVariable long borrowerId, @PathVariable long bookId) {
        return service.returnBook(borrowerId, bookId);
    }
}
