package com.collabera.library.api.dto;

import com.collabera.library.domain.Loan;
import java.time.Instant;

public record LoanResponse(Long loanId, Long borrowerId, Long bookId, Instant timestamp, String status) {
    public static LoanResponse borrowed(Loan loan) {
        return new LoanResponse(loan.getId(), loan.getBorrower().getId(), loan.getBook().getId(),
                loan.getBorrowedAt(), "BORROWED");
    }
    public static LoanResponse returned(Loan loan, Instant returnedAt) {
        return new LoanResponse(loan.getId(), loan.getBorrower().getId(), loan.getBook().getId(),
                returnedAt, "RETURNED");
    }
}
