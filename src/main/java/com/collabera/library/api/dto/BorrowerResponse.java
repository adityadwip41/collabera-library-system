package com.collabera.library.api.dto;

import com.collabera.library.domain.Borrower;
import java.time.Instant;

public record BorrowerResponse(Long id, String name, String email, Instant createdAt) {
    public static BorrowerResponse from(Borrower borrower) {
        return new BorrowerResponse(borrower.getId(), borrower.getName(), borrower.getEmail(), borrower.getCreatedAt());
    }
}
