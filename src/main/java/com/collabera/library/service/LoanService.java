package com.collabera.library.service;

import com.collabera.library.api.dto.LoanResponse;
import com.collabera.library.domain.*;
import com.collabera.library.exception.*;
import com.collabera.library.repository.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
public class LoanService {
    private final BorrowerRepository borrowers;
    private final BookRepository books;
    private final LoanRepository loans;

    public LoanService(BorrowerRepository borrowers, BookRepository books, LoanRepository loans) {
        this.borrowers = borrowers; this.books = books; this.loans = loans;
    }

    @Transactional
    public LoanResponse borrow(long borrowerId, long bookId) {
        Borrower borrower = borrowers.findById(borrowerId)
                .orElseThrow(() -> new NotFoundException("Borrower " + borrowerId + " was not found"));
        // Serialize changes to one physical copy. The database unique constraint is the final safety net.
        Book book = books.findByIdForUpdate(bookId)
                .orElseThrow(() -> new NotFoundException("Book " + bookId + " was not found"));
        if (loans.existsByBookId(bookId)) throw new ConflictException("Book " + bookId + " is already borrowed");
        try { return LoanResponse.borrowed(loans.saveAndFlush(new Loan(borrower, book))); }
        catch (DataIntegrityViolationException race) { throw new ConflictException("Book " + bookId + " is already borrowed"); }
    }

    @Transactional
    public LoanResponse returnBook(long borrowerId, long bookId) {
        if (!borrowers.existsById(borrowerId)) throw new NotFoundException("Borrower " + borrowerId + " was not found");
        // Use the same lock as borrow so concurrent borrow/return operations have deterministic outcomes.
        books.findByIdForUpdate(bookId)
                .orElseThrow(() -> new NotFoundException("Book " + bookId + " was not found"));
        Loan loan = loans.findByBookId(bookId)
                .orElseThrow(() -> new ConflictException("Book " + bookId + " is not currently borrowed"));
        if (!loan.getBorrower().getId().equals(borrowerId)) {
            throw new ConflictException("Book " + bookId + " is borrowed by another borrower");
        }
        LoanResponse response = LoanResponse.returned(loan, Instant.now());
        loans.delete(loan);
        return response;
    }
}
