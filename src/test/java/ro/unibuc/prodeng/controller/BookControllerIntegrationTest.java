package ro.unibuc.prodeng.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ro.unibuc.prodeng.IntegrationTestBase;
import ro.unibuc.prodeng.repository.BookRepository;
import ro.unibuc.prodeng.repository.UserRepository;
import ro.unibuc.prodeng.request.BookActionRequest;
import ro.unibuc.prodeng.request.CreateBookRequest;
import ro.unibuc.prodeng.request.CreateUserRequest;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("BookController Integration Tests")
class BookControllerIntegrationTest extends IntegrationTestBase {

    @Autowired private MockMvc mockMvc;
    @Autowired private BookRepository bookRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ObjectMapper objectMapper;

    @BeforeEach
    void cleanUp() {
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }

    private void createUser(String name, String email) throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateUserRequest(name, email))))
                .andExpect(status().isCreated());
    }

    private String createBook(String title, int copies) throws Exception {
        String response = mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateBookRequest(title, copies))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.availableCopies").value(copies))
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asText();
    }

    @Test
    void testBorrowBook_decreasesStock() throws Exception {
        createUser("Alice", "alice@example.com");
        String bookId = createBook("The Hobbit", 1);

        mockMvc.perform(post("/api/books/" + bookId + "/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new BookActionRequest("alice@example.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableCopies").value(0));
    }

    @Test
    void testReserveBook_addsToWaitlist() throws Exception {
        createUser("Alice", "alice@example.com");
        createUser("Bob", "bob@example.com");
        String bookId = createBook("The Hobbit", 1);

        // Alice ia ultima carte
        mockMvc.perform(post("/api/books/" + bookId + "/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new BookActionRequest("alice@example.com"))));

        // Bob o rezerva
        mockMvc.perform(post("/api/books/" + bookId + "/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new BookActionRequest("bob@example.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.queueSize").value(1));
    }
}