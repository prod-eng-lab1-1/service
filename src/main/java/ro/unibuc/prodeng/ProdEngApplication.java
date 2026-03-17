package ro.unibuc.prodeng;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import ro.unibuc.prodeng.repository.UserRepository;
import ro.unibuc.prodeng.request.CreateBookRequest;
import ro.unibuc.prodeng.request.CreateUserRequest;
import ro.unibuc.prodeng.service.BookService;
import ro.unibuc.prodeng.service.UserService;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableMongoRepositories
public class ProdEngApplication {

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(ProdEngApplication.class, args);
    }

    @PostConstruct
    public void runAfterObjectCreated() {
        if (userRepository.findByEmail("frodo@theshire.me").isEmpty()) {
            userService.createUser(new CreateUserRequest("Frodo Baggins", "frodo@theshire.me"));
            // Cream o carte cu 2 exemplare disponibile pe stoc
            bookService.createBook(new CreateBookRequest("The Fellowship of the Ring", 2));
        }
    }
}