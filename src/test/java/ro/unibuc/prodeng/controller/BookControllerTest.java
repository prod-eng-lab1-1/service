package ro.unibuc.prodeng.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ro.unibuc.prodeng.request.AssignBookRequest;
import ro.unibuc.prodeng.request.CreateBookRequest;
import ro.unibuc.prodeng.request.EditBookRequest;
import ro.unibuc.prodeng.response.BookResponse;
import ro.unibuc.prodeng.service.BookService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final BookResponse mockBook = new BookResponse("b1", "Title", false, "Luke", "luke@jedi.com");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    void testGetBooks_returnsList() throws Exception {
        when(bookService.getBooksByUserEmail("luke@jedi.com")).thenReturn(List.of(mockBook));

        mockMvc.perform(get("/api/books").param("borrowerEmail", "luke@jedi.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Title"));
    }

    @Test
    void testGetBookById_returnsBook() throws Exception {
        when(bookService.getBookById("b1")).thenReturn(mockBook);

        mockMvc.perform(get("/api/books/b1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    void testCreateBook_returnsCreated() throws Exception {
        CreateBookRequest req = new CreateBookRequest("Title", "luke@jedi.com");
        when(bookService.createBook(any(CreateBookRequest.class))).thenReturn(mockBook);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("b1"));
    }

    @Test
    void testSetBorrowed_updatesStatus() throws Exception {
        when(bookService.setBorrowed("b1", true)).thenReturn(new BookResponse("b1", "Title", true, "Luke", "luke@jedi.com"));

        mockMvc.perform(patch("/api/books/b1/borrowed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.borrowed").value(true));
    }

    @Test
    void testAssignBorrower_updatesBorrower() throws Exception {
        AssignBookRequest req = new AssignBookRequest("leia@rebel.com");
        when(bookService.assignBorrower(eq("b1"), any(AssignBookRequest.class))).thenReturn(mockBook);

        mockMvc.perform(patch("/api/books/b1/borrower")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void testEditTitle_updatesTitle() throws Exception {
        EditBookRequest req = new EditBookRequest("New Title");
        when(bookService.edit(eq("b1"), any(EditBookRequest.class))).thenReturn(mockBook);

        mockMvc.perform(patch("/api/books/b1/title")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteBook_returnsNoContent() throws Exception {
        doNothing().when(bookService).deleteBook("b1");

        mockMvc.perform(delete("/api/books/b1"))
                .andExpect(status().isNoContent());
    }
}