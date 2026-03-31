package ro.unibuc.prodeng.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ro.unibuc.prodeng.IntegrationTestBase;
import ro.unibuc.prodeng.repository.BookRepository;
import ro.unibuc.prodeng.request.CreateBookRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BookCatalogIntegrationTest extends IntegrationTestBase {

    @Autowired private MockMvc mockMvc;
    @Autowired private BookRepository bookRepository;
    @Autowired private ObjectMapper objectMapper;

    @BeforeEach
    void cleanUp() { bookRepository.deleteAll(); }

    @Test
    void testCreateBook_SavesToRealDatabase_WithCorrectStock() throws Exception {
        CreateBookRequest request = new CreateBookRequest("Clean Code", 10);

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Clean Code"));

        var savedBook = bookRepository.findAll().get(0);
        assertEquals(10, savedBook.totalCopies());
        assertEquals(10, savedBook.availableCopies());
    }
}