package com.collabera.library.repository;

import com.collabera.library.domain.Borrower;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowerRepository extends JpaRepository<Borrower, Long> {
    boolean existsByEmailIgnoreCase(String email);
}
