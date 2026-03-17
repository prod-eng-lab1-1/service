package ro.unibuc.prodeng.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ro.unibuc.prodeng.exception.EntityNotFoundException;
import ro.unibuc.prodeng.model.BookEntity;
import ro.unibuc.prodeng.model.UserEntity;
import ro.unibuc.prodeng.repository.BookRepository;
import ro.unibuc.prodeng.request.BookActionRequest;
import ro.unibuc.prodeng.request.CreateBookRequest;
import ro.unibuc.prodeng.response.BookResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private BookService bookService;

    @Test
    void testCreateBook_savesSuccessfully() {
        CreateBookRequest req = new CreateBookRequest("Title", 2);
        when(bookRepository.save(any(BookEntity.class))).thenAnswer(i -> {
            BookEntity b = i.getArgument(0);
            return new BookEntity("b1", b.title(), b.totalCopies(), b.availableCopies(), b.borrowerIds(), b.reservationQueue());
        });

        BookResponse res = bookService.createBook(req);
        assertEquals(2, res.availableCopies());
    }

    @Test
    void testBorrowBook_success_decreasesStock() throws Exception {
        BookEntity book = new BookEntity("b1", "Title", 2, 2, new ArrayList<>(), new ArrayList<>());
        UserEntity user = new UserEntity("u1", "Luke", "luke@jedi.com");
        
        when(bookRepository.findById("b1")).thenReturn(Optional.of(book));
        when(userService.getUserEntityByEmail("luke@jedi.com")).thenReturn(user);
        when(bookRepository.save(any(BookEntity.class))).thenAnswer(i -> i.getArgument(0));

        BookResponse res = bookService.borrowBook("b1", new BookActionRequest("luke@jedi.com"));
        assertEquals(1, res.availableCopies());
    }

    @Test
    void testBorrowBook_noStock_returnsUnmodified() throws Exception {
        // Cartea are stoc 0!
        BookEntity book = new BookEntity("b1", "Title", 1, 0, List.of("alt-user"), new ArrayList<>());
        UserEntity user = new UserEntity("u1", "Luke", "luke@jedi.com");

        when(bookRepository.findById("b1")).thenReturn(Optional.of(book));
        when(userService.getUserEntityByEmail("luke@jedi.com")).thenReturn(user);

        // Aici nu mai folosim assertThrows, ci doar verificam ca nu s-a schimbat nimic
        BookResponse res = bookService.borrowBook("b1", new BookActionRequest("luke@jedi.com"));
        
        assertEquals(0, res.availableCopies()); // Stocul a ramas 0
        verify(bookRepository, never()).save(any()); // Verificam ca nicio schimbare n-a fost salvata in DB
    }

    @Test
    void testReserveBook_success_addsToQueue() throws Exception {
        BookEntity book = new BookEntity("b1", "Title", 1, 0, List.of("alt-user"), new ArrayList<>());
        UserEntity user = new UserEntity("u1", "Luke", "luke@jedi.com");

        when(bookRepository.findById("b1")).thenReturn(Optional.of(book));
        when(userService.getUserEntityByEmail("luke@jedi.com")).thenReturn(user);
        when(bookRepository.save(any(BookEntity.class))).thenAnswer(i -> i.getArgument(0));

        BookResponse res = bookService.reserveBook("b1", new BookActionRequest("luke@jedi.com"));
        assertEquals(1, res.queueSize());
    }

    @Test
    void testReturnBook_withQueue_assignsToNext() throws Exception {
        List<String> borrowers = new ArrayList<>(List.of("u1"));
        List<String> queue = new ArrayList<>(List.of("u2"));
        BookEntity book = new BookEntity("b1", "Title", 1, 0, borrowers, queue);
        UserEntity user = new UserEntity("u1", "Luke", "luke@jedi.com");

        when(bookRepository.findById("b1")).thenReturn(Optional.of(book));
        when(userService.getUserEntityByEmail("luke@jedi.com")).thenReturn(user);
        when(bookRepository.save(any(BookEntity.class))).thenAnswer(i -> i.getArgument(0));

        BookResponse res = bookService.returnBook("b1", new BookActionRequest("luke@jedi.com"));
        
        assertEquals(0, res.availableCopies());
        assertEquals(0, res.queueSize());
    }
}