package ro.unibuc.prodeng.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ro.unibuc.prodeng.request.BookActionRequest;
import ro.unibuc.prodeng.request.CreateBookRequest;
import ro.unibuc.prodeng.response.BookResponse;
import ro.unibuc.prodeng.service.BookService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private BookService bookService;

    private final BookResponse mockBook = new BookResponse("b1", "Title", 2, 1, 0);

    @Test
    void testGetAllBooks() throws Exception {
        when(bookService.getAllBooks()).thenReturn(List.of(mockBook));
        mockMvc.perform(get("/api/books")).andExpect(status().isOk());
    }

    @Test
    void testCreateBook() throws Exception {
        when(bookService.createBook(any())).thenReturn(mockBook);
        mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateBookRequest("Title", 2))))
                .andExpect(status().isCreated());
    }

    @Test
    void testBorrowBook() throws Exception {
        when(bookService.borrowBook(eq("b1"), any())).thenReturn(mockBook);
        mockMvc.perform(post("/api/books/b1/borrow").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new BookActionRequest("a@a"))))
                .andExpect(status().isOk());
    }

    @Test
    void testReserveBook() throws Exception {
        when(bookService.reserveBook(eq("b1"), any())).thenReturn(mockBook);
        mockMvc.perform(post("/api/books/b1/reserve").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new BookActionRequest("a@a"))))
                .andExpect(status().isOk());
    }

    @Test
    void testReturnBook() throws Exception {
        when(bookService.returnBook(eq("b1"), any())).thenReturn(mockBook);
        mockMvc.perform(post("/api/books/b1/return").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new BookActionRequest("a@a"))))
                .andExpect(status().isOk());
    }
}