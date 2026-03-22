package ro.unibuc.prodeng.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ro.unibuc.prodeng.exception.EntityNotFoundException;
import ro.unibuc.prodeng.model.UserEntity;
import ro.unibuc.prodeng.model.UserRank;
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

    @Mock private UserRepository userRepository;
    @InjectMocks private UserService userService;

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(new UserEntity("1", "A", "a@a", 0, UserRank.BRONZE)));
        assertEquals(1, userService.getAllUsers().size());
    }

    @Test
    void testGetUserById_notFound() {
        when(userRepository.findById("99")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUserById("99"));
    }

    @Test
    void testCreateUser_alreadyExists() {
        when(userRepository.findByEmail("a@a")).thenReturn(Optional.of(new UserEntity("1", "A", "a@a", 0, UserRank.BRONZE)));
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(new CreateUserRequest("A", "a@a")));
    }

    @Test
    void testCreateUser_success() {
        when(userRepository.findByEmail("new@a")).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        UserResponse res = userService.createUser(new CreateUserRequest("New", "new@a"));
        assertEquals("New", res.name());
    }

    @Test
    void testChangeName() throws Exception {
        when(userRepository.findById("1")).thenReturn(Optional.of(new UserEntity("1", "Old", "a@a", 0, UserRank.BRONZE)));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        assertEquals("NewName", userService.changeName("1", "NewName").name());
    }

    @Test
    void testAddXpAndSave_levelUpToSilver() {
        UserEntity user = new UserEntity("1", "A", "a@a", 50, UserRank.BRONZE);
        userService.addXpAndSave(user, 50); // Devine 100 XP -> SILVER
        verify(userRepository).save(argThat(u -> u.rank() == UserRank.SILVER && u.xp() == 100));
    }

    @Test
    void testAddXpAndSave_levelUpToGold() {
        UserEntity user = new UserEntity("1", "A", "a@a", 250, UserRank.SILVER);
        userService.addXpAndSave(user, 50); // Devine 300 XP -> GOLD
        verify(userRepository).save(argThat(u -> u.rank() == UserRank.GOLD && u.xp() == 300));
    }
}