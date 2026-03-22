package ro.unibuc.prodeng.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.unibuc.prodeng.exception.EntityNotFoundException;
import ro.unibuc.prodeng.request.CreateUserRequest;
import ro.unibuc.prodeng.response.UserResponse;
import ro.unibuc.prodeng.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) throws EntityNotFoundException {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @PatchMapping("/{id}/name")
    public ResponseEntity<UserResponse> changeName(@PathVariable String id, @RequestBody String newName) throws EntityNotFoundException {
        return ResponseEntity.ok(userService.changeName(id, newName));
    }
}