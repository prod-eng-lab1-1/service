package ro.unibuc.prodeng.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import ro.unibuc.prodeng.request.AssignBookRequest;
import ro.unibuc.prodeng.request.CreateBookRequest;
import ro.unibuc.prodeng.request.EditBookRequest;
import ro.unibuc.prodeng.response.BookResponse;
import ro.unibuc.prodeng.exception.EntityNotFoundException;
import ro.unibuc.prodeng.service.BookService;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookResponse>> getBooksByUserEmail(@RequestParam String borrowerEmail) throws EntityNotFoundException {
        List<BookResponse> books = bookService.getBooksByUserEmail(borrowerEmail);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable String id) throws EntityNotFoundException {
        BookResponse book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody CreateBookRequest request) throws EntityNotFoundException {
        BookResponse book = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }

    @PatchMapping("/{id}/borrowed")
    public ResponseEntity<BookResponse> setBorrowed(@PathVariable String id, @RequestBody boolean borrowed) throws EntityNotFoundException {
        BookResponse book = bookService.setBorrowed(id, borrowed);
        return ResponseEntity.ok(book);
    }

    @PatchMapping("/{id}/borrower")
    public ResponseEntity<BookResponse> assignBorrower(@PathVariable String id, @Valid @RequestBody AssignBookRequest request) throws EntityNotFoundException {
        BookResponse book = bookService.assignBorrower(id, request);
        return ResponseEntity.ok(book);
    }

    @PatchMapping("/{id}/title")
    public ResponseEntity<BookResponse> edit(@PathVariable String id, @Valid @RequestBody EditBookRequest request) throws EntityNotFoundException {
        BookResponse book = bookService.edit(id, request);
        return ResponseEntity.ok(book);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable String id) throws EntityNotFoundException {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}