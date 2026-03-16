package ro.unibuc.prodeng.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ro.unibuc.prodeng.exception.EntityNotFoundException;
import ro.unibuc.prodeng.model.UserEntity;
import ro.unibuc.prodeng.repository.UserRepository;
import ro.unibuc.prodeng.request.CreateUserRequest;
import ro.unibuc.prodeng.response.UserResponse;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testGetAllUsers_returnsList() {
        when(userRepository.findAll()).thenReturn(List.of(new UserEntity("1", "Luke", "luke@jedi.com")));
        List<UserResponse> result = userService.getAllUsers();
        assertEquals(1, result.size());
    }

    @Test
    void testGetUserById_existingUser_returnsUser() throws Exception {
        when(userRepository.findById("1")).thenReturn(Optional.of(new UserEntity("1", "Luke", "luke@jedi.com")));
        UserResponse result = userService.getUserById("1");
        assertEquals("Luke", result.name());
    }

    @Test
    void testCreateUser_validData_savesUser() {
        CreateUserRequest req = new CreateUserRequest("Luke", "luke@jedi.com");
        when(userRepository.findByEmail("luke@jedi.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenAnswer(i -> new UserEntity("1", "Luke", "luke@jedi.com"));

        UserResponse result = userService.createUser(req);
        assertEquals("1", result.id());
    }

    @Test
    void testCreateUser_duplicateEmail_throwsException() {
        CreateUserRequest req = new CreateUserRequest("Luke", "luke@jedi.com");
        when(userRepository.findByEmail("luke@jedi.com")).thenReturn(Optional.of(new UserEntity("1", "Old", "luke@jedi.com")));

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(req));
    }

    @Test
    void testChangeName_existingUser_updatesName() throws Exception {
        when(userRepository.findById("1")).thenReturn(Optional.of(new UserEntity("1", "Old", "luke@jedi.com")));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(i -> i.getArgument(0));

        UserResponse result = userService.changeName("1", "New Name");
        assertEquals("New Name", result.name());
    }