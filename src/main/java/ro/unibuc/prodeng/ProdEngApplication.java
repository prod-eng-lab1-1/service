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
            CreateUserRequest userRequest = new CreateUserRequest("Frodo Baggins", "frodo@theshire.me");
            userService.createUser(userRequest);
            
            // Am inlocuit logica de Todo cu crearea unei carti imprumutate catre Frodo
            bookService.createBook(new CreateBookRequest("The Fellowship of the Ring", "frodo@theshire.me"));
        }
    }
}