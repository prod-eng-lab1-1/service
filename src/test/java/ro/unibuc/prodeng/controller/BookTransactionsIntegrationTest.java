package ro.unibuc.prodeng.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ro.unibuc.prodeng.IntegrationTestBase;
import ro.unibuc.prodeng.model.BookEntity;
import ro.unibuc.prodeng.model.UserEntity;
import ro.unibuc.prodeng.model.UserRank;
import ro.unibuc.prodeng.repository.BookRepository;
import ro.unibuc.prodeng.repository.UserRepository;
import ro.unibuc.prodeng.request.BookActionRequest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BookTransactionsIntegrationTest extends IntegrationTestBase {

    @Autowired private MockMvc mockMvc;
    @Autowired private BookRepository bookRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ObjectMapper objectMapper;

    @BeforeEach
    void cleanUp() { 
        bookRepository.deleteAll(); 
        userRepository.deleteAll();
    }

    @Test
    void testBorrowBook_DecreasesStockInRealDB() throws Exception {
        UserEntity user = userRepository.save(new UserEntity("u1", "Calin", "calin@jedi.com", 0, UserRank.BRONZE));
        BookEntity book = bookRepository.save(new BookEntity("b1", "Spring Action", 1, 1, new ArrayList<>(), new ArrayList<>()));

        BookActionRequest request = new BookActionRequest("calin@jedi.com");

        mockMvc.perform(post("/api/books/" + book.id() + "/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        BookEntity updatedBook = bookRepository.findById(book.id()).get();
        assertEquals(0, updatedBook.availableCopies());
        assertEquals("u1", updatedBook.borrowerIds().get(0));
    }
}