package ro.unibuc.prodeng.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ro.unibuc.prodeng.request.ChangeNameRequest;
import ro.unibuc.prodeng.request.CreateUserRequest;
import ro.unibuc.prodeng.response.UserResponse;
import ro.unibuc.prodeng.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserResponse mockUser = new UserResponse("1", "Luke", "luke@jedi.com");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testGetAllUsers_returnsList() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(mockUser));
        mockMvc.perform(get("/api/users")).andExpect(status().isOk());
    }

    @Test
    void testGetUserById_returnsUser() throws Exception {
        when(userService.getUserById("1")).thenReturn(mockUser);
        mockMvc.perform(get("/api/users/1")).andExpect(status().isOk());
    }

    @Test
    void testCreateUser_returnsCreated() throws Exception {
        CreateUserRequest req = new CreateUserRequest("Luke", "luke@jedi.com");
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(mockUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    void testUpdateUser_put_returnsOk() throws Exception {
        ChangeNameRequest req = new ChangeNameRequest("New Name");
        when(userService.changeName("1", "New Name")).thenReturn(mockUser);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void testChangeName_patch_returnsOk() throws Exception {
        ChangeNameRequest req = new ChangeNameRequest("New Name");
        when(userService.changeName("1", "New Name")).thenReturn(mockUser);

        mockMvc.perform(patch("/api/users/1/name")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteUser_returnsNoContent() throws Exception {
        doNothing().when(userService).deleteUser("1");
        mockMvc.perform(delete("/api/users/1")).andExpect(status().isNoContent());
    }

    @Test
    void testGetUserByEmail_returnsUser() throws Exception {
        when(userService.getUserByEmail("luke@jedi.com")).thenReturn(mockUser);
        mockMvc.perform(get("/api/users/by-email").param("email", "luke@jedi.com"))
                .andExpect(status().isOk());
    }
}