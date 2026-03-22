package ro.unibuc.prodeng;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.unibuc.prodeng.repository.UserRepository;
import ro.unibuc.prodeng.request.CreateBookRequest;
import ro.unibuc.prodeng.request.CreateUserRequest;
import ro.unibuc.prodeng.service.BookService;
import ro.unibuc.prodeng.service.UserService;

@Component
public class DataSetup {

    @Autowired private UserRepository userRepository;
    @Autowired private UserService userService;
    @Autowired private BookService bookService;

    @PostConstruct
    public void runAfterObjectCreated() {
        if (userRepository.findByEmail("frodo@theshire.me").isEmpty()) {
            userService.createUser(new CreateUserRequest("Frodo Baggins", "frodo@theshire.me"));
            bookService.createBook(new CreateBookRequest("The Fellowship of the Ring", 2));
        }
    }
}