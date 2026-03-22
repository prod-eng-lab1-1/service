package ro.unibuc.prodeng.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ro.unibuc.prodeng.request.CreateUserRequest;
import ro.unibuc.prodeng.response.UserResponse;
import ro.unibuc.prodeng.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private UserService userService;

    private final UserResponse mockUser = new UserResponse("1", "Luke", "luke@jedi.com");

    @Test
    void testGetAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(mockUser));
        mockMvc.perform(get("/api/users")).andExpect(status().isOk());
    }

    @Test
    void testGetUserById() throws Exception {
        when(userService.getUserById("1")).thenReturn(mockUser);
        mockMvc.perform(get("/api/users/1")).andExpect(status().isOk());
    }

    @Test
    void testCreateUser() throws Exception {
        when(userService.createUser(any())).thenReturn(mockUser);
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateUserRequest("Luke", "luke@jedi.com"))))
                .andExpect(status().isCreated());
    }

    @Test
    void testChangeName() throws Exception {
        when(userService.changeName(eq("1"), any())).thenReturn(mockUser);
        mockMvc.perform(patch("/api/users/1/name").contentType(MediaType.APPLICATION_JSON)
                .content("NewName")).andExpect(status().isOk());
    }
}