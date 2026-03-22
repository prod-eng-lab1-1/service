package ro.unibuc.prodeng.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.unibuc.prodeng.exception.EntityNotFoundException;
import ro.unibuc.prodeng.request.BookActionRequest;
import ro.unibuc.prodeng.request.CreateBookRequest;
import ro.unibuc.prodeng.response.BookResponse;
import ro.unibuc.prodeng.service.BookService;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody CreateBookRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(request));
    }

    @PostMapping("/{id}/borrow")
    public ResponseEntity<BookResponse> borrowBook(@PathVariable String id, @Valid @RequestBody BookActionRequest request) throws EntityNotFoundException {
        return ResponseEntity.ok(bookService.borrowBook(id, request));
    }

    @PostMapping("/{id}/reserve")
    public ResponseEntity<BookResponse> reserveBook(@PathVariable String id, @Valid @RequestBody BookActionRequest request) throws EntityNotFoundException {
        return ResponseEntity.ok(bookService.reserveBook(id, request));
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<BookResponse> returnBook(@PathVariable String id, @Valid @RequestBody BookActionRequest request) throws EntityNotFoundException {
        return ResponseEntity.ok(bookService.returnBook(id, request));
    }
}