package ro.unibuc.prodeng.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ro.unibuc.prodeng.model.BookEntity;
import ro.unibuc.prodeng.repository.BookRepository;
import ro.unibuc.prodeng.model.UserEntity;
import ro.unibuc.prodeng.request.AssignBookRequest;
import ro.unibuc.prodeng.request.CreateBookRequest;
import ro.unibuc.prodeng.request.EditBookRequest;
import ro.unibuc.prodeng.response.BookResponse;
import ro.unibuc.prodeng.exception.EntityNotFoundException;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserService userService;

    public List<BookResponse> getBooksByUserEmail(String email) throws EntityNotFoundException {
        UserEntity user = userService.getUserEntityByEmail(email);
        List<BookEntity> books = bookRepository.findByBorrowerUserId(user.id());
        return books.stream()
                .map(book -> toResponse(book, user))
                .toList();
    }

    public BookResponse getBookById(String id) throws EntityNotFoundException {
        BookEntity book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        UserEntity borrower = userService.getUserEntityById(book.borrowerUserId());
        return toResponse(book, borrower);
    }

    public BookResponse createBook(CreateBookRequest request) throws EntityNotFoundException {
        UserEntity borrower = userService.getUserEntityByEmail(request.borrowerEmail());
        BookEntity book = new BookEntity(
                null, // ID va fi generat automat de MongoDB
                request.title(),
                false, // O carte nou adaugată nu e neapărat împrumutată by default
                borrower.id()
        );
        BookEntity saved = bookRepository.save(book);
        return toResponse(saved, borrower);
    }

    // Funcționalitate pentru Returnare / Modificare status "împrumutat"
    public BookResponse setBorrowed(String id, boolean borrowed) throws EntityNotFoundException {
        BookEntity existing = getEntityById(id);
        BookEntity updated = new BookEntity(id, existing.title(), borrowed, existing.borrowerUserId());
        BookEntity saved = bookRepository.save(updated);
        UserEntity borrower = userService.getUserEntityById(saved.borrowerUserId());
        return toResponse(saved, borrower);
    }

    // Funcționalitate pentru a împrumuta o carte către un membru nou
    public BookResponse assignBorrower(String id, AssignBookRequest request) throws EntityNotFoundException {
        BookEntity existing = getEntityById(id);
        UserEntity newBorrower = userService.getUserEntityByEmail(request.newBorrowerEmail());
        BookEntity updated = new BookEntity(id, existing.title(), existing.borrowed(), newBorrower.id());
        BookEntity saved = bookRepository.save(updated);
        return toResponse(saved, newBorrower);
    }

    // Funcționalitate pentru a updata detaliile cărții (ex: titlul)
    public BookResponse edit(String id, EditBookRequest request) throws EntityNotFoundException {
        BookEntity existing = getEntityById(id);
        BookEntity updated = new BookEntity(id, request.title(), existing.borrowed(), existing.borrowerUserId());
        BookEntity saved = bookRepository.save(updated);
        UserEntity borrower = userService.getUserEntityById(saved.borrowerUserId());
        return toResponse(saved, borrower);
    }

    public void deleteBook(String id) throws EntityNotFoundException {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException(id);
        }
        bookRepository.deleteById(id);
    }

    private BookEntity getEntityById(String id) throws EntityNotFoundException {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
    }

    private BookResponse toResponse(BookEntity book, UserEntity borrower) {
        return new BookResponse(
                book.id(),
                book.title(),
                book.borrowed(),
                borrower.name(),
                borrower.email()
        );
    }
}