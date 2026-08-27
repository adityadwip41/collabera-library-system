package com.collabera.library.service;

import com.collabera.library.api.dto.*;
import com.collabera.library.domain.Borrower;
import com.collabera.library.exception.ConflictException;
import com.collabera.library.repository.BorrowerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BorrowerService {
    private final BorrowerRepository borrowers;
    public BorrowerService(BorrowerRepository borrowers) { this.borrowers = borrowers; }

    @Transactional
    public BorrowerResponse register(RegisterBorrowerRequest request) {
        String email = request.email().trim().toLowerCase();
        if (borrowers.existsByEmailIgnoreCase(email)) throw new ConflictException("A borrower with this email already exists");
        return BorrowerResponse.from(borrowers.save(new Borrower(request.name().trim(), email)));
    }
}
