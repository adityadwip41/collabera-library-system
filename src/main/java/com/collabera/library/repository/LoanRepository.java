package com.collabera.library.repository;

import com.collabera.library.domain.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    boolean existsByBookId(Long bookId);
    Optional<Loan> findByBookId(Long bookId);

    @Query("select loan.book.id from Loan loan where loan.book.id in :bookIds")
    List<Long> findBorrowedBookIds(@Param("bookIds") Collection<Long> bookIds);
}
