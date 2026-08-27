package com.collabera.library;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LibraryApiIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    private long borrowerId;
    private long bookId;

    @BeforeEach
    void createFixtures() throws Exception {
        borrowerId = idFrom(postJson("/api/v1/borrowers", """
                {"name":"Alice Example","email":"alice-%d@example.com"}
                """.formatted(System.nanoTime())));
        bookId = idFrom(postJson("/api/v1/books", """
                {"isbn":"9780132350884","title":"Clean Code","author":"Robert C. Martin"}
                """));
    }

    @Test
    void registersAndListsBookCopies() throws Exception {
        mvc.perform(get("/api/v1/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id", hasItem((int) bookId)))
                .andExpect(jsonPath("$.content[?(@.id == %d)].available".formatted(bookId), hasItem(true)));
    }

    @Test
    void allowsMultipleCopiesWithSameIsbn() throws Exception {
        mvc.perform(post("/api/v1/books").contentType(MediaType.APPLICATION_JSON).content("""
                {"isbn":"9780132350884","title":"Clean Code","author":"Robert C. Martin"}
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", not(bookId)));
    }

    @Test
    void rejectsConflictingMetadataForExistingIsbn() throws Exception {
        mvc.perform(post("/api/v1/books").contentType(MediaType.APPLICATION_JSON).content("""
                {"isbn":"9780132350884","title":"Wrong Title","author":"Someone Else"}
                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflict"));
    }

    @Test
    void borrowsAndReturnsBook() throws Exception {
        mvc.perform(post("/api/v1/borrowers/{borrowerId}/borrowed-books/{bookId}", borrowerId, bookId))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("BORROWED"));
        mvc.perform(delete("/api/v1/borrowers/{borrowerId}/borrowed-books/{bookId}", borrowerId, bookId))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("RETURNED"));
    }

    @Test
    void preventsSecondBorrowerFromBorrowingSameCopy() throws Exception {
        long otherBorrower = idFrom(postJson("/api/v1/borrowers", """
                {"name":"Bob Example","email":"bob-%d@example.com"}
                """.formatted(System.nanoTime())));
        mvc.perform(post("/api/v1/borrowers/{borrowerId}/borrowed-books/{bookId}", borrowerId, bookId))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/v1/borrowers/{borrowerId}/borrowed-books/{bookId}", otherBorrower, bookId))
                .andExpect(status().isConflict());
    }

    @Test
    void returnsProblemDetailsForInvalidRequest() throws Exception {
        mvc.perform(post("/api/v1/borrowers").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"email\":\"invalid\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    void returnsProblemDetailsForMalformedJson() throws Exception {
        mvc.perform(post("/api/v1/books").contentType(MediaType.APPLICATION_JSON).content("{broken"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Malformed or unreadable JSON request"));
    }

    @Test
    void rejectsDuplicateBorrowerEmailIgnoringCase() throws Exception {
        String email = "duplicate-%d@example.com".formatted(System.nanoTime());
        postJson("/api/v1/borrowers", "{\"name\":\"First\",\"email\":\"%s\"}".formatted(email));
        mvc.perform(post("/api/v1/borrowers").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Second\",\"email\":\"%s\"}".formatted(email.toUpperCase())))
                .andExpect(status().isConflict());
    }

    @Test
    void onlyCurrentBorrowerCanReturnCopy() throws Exception {
        long otherBorrower = idFrom(postJson("/api/v1/borrowers", """
                {"name":"Charlie Example","email":"charlie-%d@example.com"}
                """.formatted(System.nanoTime())));
        mvc.perform(post("/api/v1/borrowers/{borrowerId}/borrowed-books/{bookId}", borrowerId, bookId))
                .andExpect(status().isCreated());
        mvc.perform(delete("/api/v1/borrowers/{borrowerId}/borrowed-books/{bookId}", otherBorrower, bookId))
                .andExpect(status().isConflict());
    }

    private String postJson(String path, String json) throws Exception {
        return mvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
    }

    private long idFrom(String json) throws Exception { return mapper.readTree(json).get("id").asLong(); }
}
