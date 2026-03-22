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
import ro.unibuc.prodeng.request.BookActionRequest;
import ro.unibuc.prodeng.request.CreateBookRequest;
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
    private final BookResponse mockBook = new BookResponse("b1", "Title", 2, 2, 0);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    void testGetAllBooks() throws Exception {
        when(bookService.getAllBooks()).thenReturn(List.of(mockBook));
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateBook() throws Exception {
        CreateBookRequest req = new CreateBookRequest("Title", 2);
        when(bookService.createBook(any())).thenReturn(mockBook);
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    void testBorrowBook() throws Exception {
        when(bookService.borrowBook(eq("b1"), any())).thenReturn(mockBook);
        mockMvc.perform(post("/api/books/b1/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new BookActionRequest("test@test.com"))))
                .andExpect(status().isOk());
    }

    @Test
    void testReturnBook() throws Exception {
        when(bookService.returnBook(eq("b1"), any())).thenReturn(mockBook);
        mockMvc.perform(post("/api/books/b1/return")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new BookActionRequest("test@test.com"))))
                .andExpect(status().isOk());
    }

    @Test
    void testReserveBook() throws Exception {
        when(bookService.reserveBook(eq("b1"), any())).thenReturn(mockBook);
        mockMvc.perform(post("/api/books/b1/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new BookActionRequest("test@test.com"))))
                .andExpect(status().isOk());
    }
}