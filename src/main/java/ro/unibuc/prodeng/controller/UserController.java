package ro.unibuc.prodeng.controller;

import io.micrometer.core.instrument.Timer;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.unibuc.prodeng.exception.EntityNotFoundException;
import ro.unibuc.prodeng.request.CreateUserRequest;
import ro.unibuc.prodeng.response.UserResponse;
import ro.unibuc.prodeng.service.UserService;
import ro.unibuc.prodeng.service.MetricsService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private MetricsService metricsService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        logger.info("Fetching all users");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) throws EntityNotFoundException {
        logger.info("Fetching user with id: {}", id);
        Timer.Sample sample = Timer.start(); // Pornim cronometrul
        try {
            UserResponse response = userService.getUserById(id);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            logger.error("User not found: {}", id, e);
            metricsService.recordError();
            throw e;
        } finally {
            sample.stop(metricsService.getUserLookupTimer()); // Oprim cronometrul indiferent de rezultat
        }
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        // AICI AM MODIFICAT: request.name() in loc de request.getName()
        logger.info("Creating user with name: {}", request.name()); 
        try {
            UserResponse response = userService.createUser(request);
            metricsService.recordUserCreated(); 
            // AICI AM MODIFICAT: response.id() in loc de response.getId()
            logger.info("User created successfully with id: {}", response.id()); 
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Failed to create user", e);
            metricsService.recordError(); 
            throw e;
        }
    }

    @PatchMapping("/{id}/name")
    public ResponseEntity<UserResponse> changeName(@PathVariable String id, @RequestBody String newName) throws EntityNotFoundException {
        logger.info("Changing name for user id: {} to {}", id, newName);
        try {
            return ResponseEntity.ok(userService.changeName(id, newName));
        } catch (EntityNotFoundException e) {
            logger.error("Failed to change name, user not found: {}", id, e);
            metricsService.recordError();
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        logger.info("Deleting user with id: {}", id);
        userService.deleteUser(id);
        metricsService.recordUserDeleted(); // <-- Metrica
        return ResponseEntity.noContent().build();
    }
}