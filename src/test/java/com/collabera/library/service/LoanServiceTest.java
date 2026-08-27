package com.collabera.library.service;

import com.collabera.library.domain.*;
import com.collabera.library.exception.*;
import com.collabera.library.repository.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoanServiceTest {
    @Mock BorrowerRepository borrowers;
    @Mock BookRepository books;
    @Mock LoanRepository loans;
    @InjectMocks LoanService service;

    @BeforeEach void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void reportsMissingBorrowerBeforeCheckingBook() {
        when(borrowers.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.borrow(99, 1))
                .isInstanceOf(NotFoundException.class).hasMessageContaining("Borrower 99");
        verifyNoInteractions(books, loans);
    }

    @Test
    void rejectsAlreadyBorrowedBook() {
        Borrower borrower = new Borrower("Alice", "alice@example.com");
        Book book = new Book(new BookCatalog("9780132350884", "Clean Code", "Robert C. Martin"));
        when(borrowers.findById(1L)).thenReturn(Optional.of(borrower));
        when(books.findByIdForUpdate(2L)).thenReturn(Optional.of(book));
        when(loans.existsByBookId(2L)).thenReturn(true);
        assertThatThrownBy(() -> service.borrow(1, 2))
                .isInstanceOf(ConflictException.class).hasMessageContaining("already borrowed");
        verify(loans, never()).saveAndFlush(any());
    }
}
