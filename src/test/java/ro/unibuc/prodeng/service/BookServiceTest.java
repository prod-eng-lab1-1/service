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
import ro.unibuc.prodeng.request.AssignBookRequest;
import ro.unibuc.prodeng.request.CreateBookRequest;
import ro.unibuc.prodeng.request.EditBookRequest;
import ro.unibuc.prodeng.response.BookResponse;

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
    void testGetBooksByUserEmail_existingUser_returnsBooks() throws EntityNotFoundException {
        // Arrange
        UserEntity user = new UserEntity("1", "Luke", "luke@jedi.com");
        BookEntity book = new BookEntity("b1", "Jedi Guide", false, "1");

        when(userService.getUserEntityByEmail("luke@jedi.com")).thenReturn(user);
        when(bookRepository.findByBorrowerUserId("1")).thenReturn(List.of(book));

        // Act
        List<BookResponse> result = bookService.getBooksByUserEmail("luke@jedi.com");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Jedi Guide", result.get(0).title());
    }

    @Test
    void testGetBookById_existingBook_returnsBook() throws EntityNotFoundException {
        // Arrange
        BookEntity book = new BookEntity("b1", "Jedi Guide", false, "1");
        UserEntity user = new UserEntity("1", "Luke", "luke@jedi.com");

        when(bookRepository.findById("b1")).thenReturn(Optional.of(book));
        when(userService.getUserEntityById("1")).thenReturn(user);

        // Act
        BookResponse result = bookService.getBookById("b1");

        // Assert
        assertEquals("Jedi Guide", result.title());
        assertEquals("Luke", result.borrowerName());
    }

    @Test
    void testGetBookById_nonExistingBook_throwsException() {
        // Arrange
        when(bookRepository.findById("invalid")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> bookService.getBookById("invalid"));
    }

    @Test
    void testCreateBook_validData_returnsSavedBook() throws EntityNotFoundException {
        // Arrange
        UserEntity user = new UserEntity("1", "Luke", "luke@jedi.com");
        CreateBookRequest request = new CreateBookRequest("Jedi Guide", "luke@jedi.com");

        when(userService.getUserEntityByEmail("luke@jedi.com")).thenReturn(user);
        when(bookRepository.save(any(BookEntity.class))).thenAnswer(i -> {
            BookEntity b = i.getArgument(0);
            return new BookEntity("b1", b.title(), b.borrowed(), b.borrowerUserId());
        });

        // Act
        BookResponse result = bookService.createBook(request);

        // Assert
        assertEquals("b1", result.id());
        assertEquals("Jedi Guide", result.title());
    }

    @Test
    void testSetBorrowed_existingBook_updatesStatus() throws EntityNotFoundException {
        // Arrange
        BookEntity book = new BookEntity("b1", "Jedi Guide", false, "1");
        UserEntity user = new UserEntity("1", "Luke", "luke@jedi.com");

        when(bookRepository.findById("b1")).thenReturn(Optional.of(book));
        when(bookRepository.save(any(BookEntity.class))).thenAnswer(i -> i.getArgument(0));
        when(userService.getUserEntityById("1")).thenReturn(user);

        // Act
        BookResponse result = bookService.setBorrowed("b1", true);

        // Assert
        assertTrue(result.borrowed());
    }

    @Test
    void testAssignBorrower_existingBookAndUser_updatesBorrower() throws EntityNotFoundException {
        // Arrange
        BookEntity book = new BookEntity("b1", "Jedi Guide", false, "1");
        UserEntity newUser = new UserEntity("2", "Leia", "leia@rebel.com");
        AssignBookRequest request = new AssignBookRequest("leia@rebel.com");

        when(bookRepository.findById("b1")).thenReturn(Optional.of(book));
        when(userService.getUserEntityByEmail("leia@rebel.com")).thenReturn(newUser);
        when(bookRepository.save(any(BookEntity.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        BookResponse result = bookService.assignBorrower("b1", request);

        // Assert
        assertEquals("Leia", result.borrowerName());
    }

    @Test
    void testEdit_existingBook_updatesTitle() throws EntityNotFoundException {
        // Arrange
        BookEntity book = new BookEntity("b1", "Old Title", false, "1");
        UserEntity user = new UserEntity("1", "Luke", "luke@jedi.com");
        EditBookRequest request = new EditBookRequest("New Title");

        when(bookRepository.findById("b1")).thenReturn(Optional.of(book));
        when(bookRepository.save(any(BookEntity.class))).thenAnswer(i -> i.getArgument(0));
        when(userService.getUserEntityById("1")).thenReturn(user);

        // Act
        BookResponse result = bookService.edit("b1", request);

        // Assert
        assertEquals("New Title", result.title());
    }

    @Test
    void testDeleteBook_existingBook_deletesSuccessfully() throws EntityNotFoundException {
        // Arrange
        when(bookRepository.existsById("b1")).thenReturn(true);

        // Act
        bookService.deleteBook("b1");

        // Assert
        verify(bookRepository, times(1)).deleteById("b1");
    }

    @Test
    void testDeleteBook_nonExistingBook_throwsException() {
        // Arrange
        when(bookRepository.existsById("invalid")).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> bookService.deleteBook("invalid"));
    }
}