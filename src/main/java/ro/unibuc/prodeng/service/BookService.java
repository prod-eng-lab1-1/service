package ro.unibuc.prodeng.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.unibuc.prodeng.exception.EntityNotFoundException;
import ro.unibuc.prodeng.model.BookEntity;
import ro.unibuc.prodeng.model.UserEntity;
import ro.unibuc.prodeng.repository.BookRepository;
import ro.unibuc.prodeng.request.BookActionRequest;
import ro.unibuc.prodeng.request.CreateBookRequest;
import ro.unibuc.prodeng.response.BookResponse;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserService userService;

    public BookResponse createBook(CreateBookRequest request) {
        BookEntity book = new BookEntity(
                null,
                request.title(),
                request.copies(),
                request.copies(), 
                new ArrayList<>(),
                new ArrayList<>()
        );
        return toResponse(bookRepository.save(book));
    }

    public BookResponse borrowBook(String bookId, BookActionRequest request) throws EntityNotFoundException {
        BookEntity book = getEntityById(bookId);
        UserEntity user = userService.getUserEntityByEmail(request.userEmail());

        if (book.borrowerIds().contains(user.id())) {
            System.out.println("AVERTISMENT: Utilizatorul a împrumutat deja această carte.");
            return toResponse(book);
        }

        long currentBorrowedBooks = bookRepository.findAll().stream()
                .filter(b -> b.borrowerIds().contains(user.id()))
                .count();
        if (currentBorrowedBooks >= 3) {
            System.out.println("AVERTISMENT: Utilizatorul a atins limita de 3 cărți.");
            return toResponse(book);
        }

        if (book.availableCopies() <= 0) {
            System.out.println("AVERTISMENT: Stoc epuizat. Împrumutul a fost ignorat.");
            return toResponse(book);
        }

        List<String> updatedBorrowers = new ArrayList<>(book.borrowerIds());
        updatedBorrowers.add(user.id());

        BookEntity updatedBook = new BookEntity(
                book.id(), book.title(), book.totalCopies(),
                book.availableCopies() - 1, updatedBorrowers, book.reservationQueue()
        );

        return toResponse(bookRepository.save(updatedBook));
    }

    public BookResponse reserveBook(String bookId, BookActionRequest request) throws EntityNotFoundException {
        BookEntity book = getEntityById(bookId);
        UserEntity user = userService.getUserEntityByEmail(request.userEmail());

        if (book.borrowerIds().contains(user.id())) {
            System.out.println("AVERTISMENT: Utilizatorul are deja cartea, nu o poate rezerva.");
            return toResponse(book);
        }
        if (book.reservationQueue().contains(user.id())) {
            System.out.println("AVERTISMENT: Utilizatorul este deja la coada.");
            return toResponse(book);
        }
        if (book.availableCopies() > 0) {
            System.out.println("AVERTISMENT: Cartea este pe stoc. Rezervarea a fost ignorată.");
            return toResponse(book);
        }

        List<String> updatedQueue = new ArrayList<>(book.reservationQueue());
        updatedQueue.add(user.id());

        BookEntity updatedBook = new BookEntity(
                book.id(), book.title(), book.totalCopies(),
                book.availableCopies(), book.borrowerIds(), updatedQueue
        );

        return toResponse(bookRepository.save(updatedBook));
    }

    public BookResponse returnBook(String bookId, BookActionRequest request) throws EntityNotFoundException {
        BookEntity book = getEntityById(bookId);
        UserEntity user = userService.getUserEntityByEmail(request.userEmail());

        if (!book.borrowerIds().contains(user.id())) {
            System.out.println("AVERTISMENT: Utilizatorul nu deține această carte. Returnare anulată.");
            return toResponse(book);
        }

        List<String> updatedBorrowers = new ArrayList<>(book.borrowerIds());
        updatedBorrowers.remove(user.id()); 

        List<String> updatedQueue = new ArrayList<>(book.reservationQueue());
        int newAvailableCopies = book.availableCopies() + 1;

        if (!updatedQueue.isEmpty()) {
            String nextUserInLineId = updatedQueue.remove(0);
            updatedBorrowers.add(nextUserInLineId); 
            newAvailableCopies--; 

            System.out.println("NOTIFICARE: Cartea " + book.title() + " a fost asignată automat utilizatorului cu ID " + nextUserInLineId);
        }

        BookEntity updatedBook = new BookEntity(
                book.id(), book.title(), book.totalCopies(),
                newAvailableCopies, updatedBorrowers, updatedQueue
        );

        return toResponse(bookRepository.save(updatedBook));
    }

    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream().map(this::toResponse).toList();
    }

    private BookEntity getEntityById(String id) throws EntityNotFoundException {
        return bookRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
    }

    private BookResponse toResponse(BookEntity book) {
        return new BookResponse(
                book.id(),
                book.title(),
                book.totalCopies(),
                book.availableCopies(),
                book.reservationQueue().size()
        );
    }
}