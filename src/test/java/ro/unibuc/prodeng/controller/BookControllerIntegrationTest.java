package ro.unibuc.prodeng.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import ro.unibuc.prodeng.IntegrationTestBase;
import ro.unibuc.prodeng.repository.BookRepository;
import ro.unibuc.prodeng.repository.UserRepository;
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

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanUp() {
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }

    private void createUser(String name, String email) throws Exception {
        CreateUserRequest request = new CreateUserRequest(name, email);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    private String createBook(String title, String borrowerEmail) throws Exception {
        CreateBookRequest request = new CreateBookRequest(title, borrowerEmail);

        String response = mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.borrowed").value(false))
                .andExpect(jsonPath("$.borrowerEmail").value(borrowerEmail))
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asText();
    }

    @Test
    void testCreateAndGetBook_validBookCreation_retrievesBookSuccessfully() throws Exception {
        createUser("Alice", "alice@example.com");
        String bookId = createBook("The Hobbit", "alice@example.com");

        mockMvc.perform(get("/api/books/" + bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("The Hobbit"))
                .andExpect(jsonPath("$.borrowed").value(false))
                .andExpect(jsonPath("$.borrowerName").value("Alice"))
                .andExpect(jsonPath("$.borrowerEmail").value("alice@example.com"));
    }

    @Test
    void testGetBooksByUser_multipleUsersWithDifferentBooks_filtersCorrectly() throws Exception {
        createUser("Alice", "alice@example.com");
        createUser("Bob", "bob@example.com");
        createBook("The Hobbit", "alice@example.com");
        createBook("1984", "alice@example.com");
        createBook("Dune", "bob@example.com");

        mockMvc.perform(get("/api/books").param("borrowerEmail", "alice@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(get("/api/books").param("borrowerEmail", "bob@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testSetBorrowed_toggleBorrowedStatus_updatesStatusCorrectly() throws Exception {
        createUser("Alice", "alice@example.com");
        String bookId = createBook("The Hobbit", "alice@example.com");

        mockMvc.perform(patch("/api/books/" + bookId + "/borrowed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.borrowed").value(true));

        mockMvc.perform(patch("/api/books/" + bookId + "/borrowed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.borrowed").value(false));
    }

    @Test
    void testAssign_reassignToDifferentUser_updateBorrowerSuccessfully() throws Exception {
        createUser("Alice", "alice@example.com");
        createUser("Bob", "bob@example.com");
        String bookId = createBook("The Hobbit", "alice@example.com");

        mockMvc.perform(patch("/api/books/" + bookId + "/borrower")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newBorrowerEmail\":\"bob@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.borrowerName").value("Bob"))
                .andExpect(jsonPath("$.borrowerEmail").value("bob@example.com"));
    }

    @Test
    void testEditTitle_validNewTitle_updatesTitleSuccessfully() throws Exception {
        createUser("Alice", "alice@example.com");
        String bookId = createBook("The Hobbit", "alice@example.com");

        mockMvc.perform(patch("/api/books/" + bookId + "/title")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"The Lord of the Rings\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("The Lord of the Rings"));
    }

    @Test
    void testDeleteBook_existingBook_deletesSuccessfully() throws Exception {
        createUser("Alice", "alice@example.com");
        String bookId = createBook("The Hobbit", "alice@example.com");

        mockMvc.perform(delete("/api/books/" + bookId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/books").param("borrowerEmail", "alice@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetBookById_nonExistentBook_returnsNotFound() throws Exception {
        String nonExistentId = "nonexistent-book-id";

        mockMvc.perform(get("/api/books/" + nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Entity: " + nonExistentId + " was not found"));
    }

    @Test
    void testCreateBook_nonExistentBorrower_returnsNotFound() throws Exception {
        CreateBookRequest request = new CreateBookRequest("The Hobbit", "nonexistent@example.com");

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testSetBorrowed_nonExistentBook_returnsNotFound() throws Exception {
        String nonExistentId = "nonexistent-book-id";

        mockMvc.perform(patch("/api/books/" + nonExistentId + "/borrowed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("true"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Entity: " + nonExistentId + " was not found"));
    }
}