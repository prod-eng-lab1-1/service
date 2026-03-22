package ro.unibuc.prodeng.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ro.unibuc.prodeng.exception.EntityNotFoundException;
import ro.unibuc.prodeng.model.BookEntity;
import ro.unibuc.prodeng.model.UserEntity;
import ro.unibuc.prodeng.model.UserRank;
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

    @Mock private BookRepository bookRepository;
    @Mock private UserService userService;
    @InjectMocks private BookService bookService;

    @Test
    void testCreateAndGetAllBooks() {
        when(bookRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        BookResponse res = bookService.createBook(new CreateBookRequest("Title", 5));
        assertEquals(5, res.availableCopies());

        when(bookRepository.findAll()).thenReturn(List.of(new BookEntity("1", "T", 1, 1, new ArrayList<>(), new ArrayList<>())));
        assertEquals(1, bookService.getAllBooks().size());
    }

    @Test
    void testBorrowBook_errors() throws Exception {
        BookEntity book = new BookEntity("b1", "T", 1, 0, List.of("u1"), new ArrayList<>());
        UserEntity user = new UserEntity("u1", "A", "a@a", 0, UserRank.BRONZE);

        when(bookRepository.findById("b1")).thenReturn(Optional.of(book));
        when(userService.getUserEntityByEmail("a@a")).thenReturn(user);

        assertThrows(IllegalArgumentException.class, () -> bookService.borrowBook("b1", new BookActionRequest("a@a")));

        book = new BookEntity("b1", "T", 1, 0, new ArrayList<>(), new ArrayList<>());
        when(bookRepository.findById("b1")).thenReturn(Optional.of(book));
        assertThrows(IllegalStateException.class, () -> bookService.borrowBook("b1", new BookActionRequest("a@a")));
    }

    @Test
    void testBorrowBook_limitReached() throws Exception {
        BookEntity book = new BookEntity("b1", "T", 1, 1, new ArrayList<>(), new ArrayList<>());
        UserEntity user = new UserEntity("u1", "A", "a@a", 0, UserRank.BRONZE); // BRONZE = limit 1
        BookEntity borrowedBook = new BookEntity("b2", "T2", 1, 0, List.of("u1"), new ArrayList<>());

        when(bookRepository.findById("b1")).thenReturn(Optional.of(book));
        when(userService.getUserEntityByEmail("a@a")).thenReturn(user);
        when(bookRepository.findAll()).thenReturn(List.of(borrowedBook)); // Deja are o carte

        assertThrows(IllegalStateException.class, () -> bookService.borrowBook("b1", new BookActionRequest("a@a")));
    }

    @Test
    void testReserveBook_prioritySorting() throws Exception {
        // Coada are un user BRONZE
        List<String> queue = new ArrayList<>(List.of("bronzeUser"));
        BookEntity book = new BookEntity("b1", "T", 1, 0, new ArrayList<>(), queue);
        
        // Vine un user GOLD sa rezerve
        UserEntity goldUser = new UserEntity("goldUser", "G", "g@g", 500, UserRank.GOLD);
        UserEntity bronzeUser = new UserEntity("bronzeUser", "B", "b@b", 0, UserRank.BRONZE);

        when(bookRepository.findById("b1")).thenReturn(Optional.of(book));
        when(userService.getUserEntityByEmail("g@g")).thenReturn(goldUser);
        when(userService.getUserEntityById("goldUser")).thenReturn(goldUser);
        when(userService.getUserEntityById("bronzeUser")).thenReturn(bronzeUser);
        when(bookRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        bookService.reserveBook("b1", new BookActionRequest("g@g"));
        
        // Verificam ca sortarea si-a facut treaba (GOLD e bagat in fata)
        verify(bookRepository).save(argThat(b -> b.reservationQueue().get(0).equals("goldUser")));
    }

    @Test
    void testReturnBook_withQueue() throws Exception {
        List<String> borrowers = new ArrayList<>(List.of("u1"));
        List<String> queue = new ArrayList<>(List.of("u2"));
        BookEntity book = new BookEntity("b1", "T", 1, 0, borrowers, queue);
        UserEntity user = new UserEntity("u1", "A", "a@a", 0, UserRank.BRONZE);

        when(bookRepository.findById("b1")).thenReturn(Optional.of(book));
        when(userService.getUserEntityByEmail("a@a")).thenReturn(user);
        when(bookRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        BookResponse res = bookService.returnBook("b1", new BookActionRequest("a@a"));
        
        // Cartea se duce la u2. Stocul ramane 0. Coada scade.
        assertEquals(0, res.availableCopies());
        assertEquals(0, res.queueSize());
        verify(userService).addXpAndSave(user, 50); // XP acordat
    }
}