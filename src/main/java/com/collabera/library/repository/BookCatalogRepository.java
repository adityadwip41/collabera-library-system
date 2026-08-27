package com.collabera.library.repository;

import com.collabera.library.domain.BookCatalog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookCatalogRepository extends JpaRepository<BookCatalog, String> {}
