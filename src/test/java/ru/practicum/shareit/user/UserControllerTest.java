package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;
    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();
    private BookingDto returnObject;

    @BeforeEach
    void setUp() {
        mapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        returnObject = BookingDto.builder().id(1L).build();
    }

    @Test
    void createUser() throws Exception {
        User user = new User();
        user.setName("testName");
        user.setEmail("testEmail@test.test");

        when(userService.createUser(any())).thenReturn(UserDto.builder().build());
        mockMvc.perform(post("/users").content(mapper.writeValueAsString(user)).header(BookingController.HEADER_USER_ID_FIELD, "1").characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
    }

    @Test
    void patchUser() throws Exception {
        User user = new User();
        user.setName("testName");
        user.setEmail("testEmail@test.test");

        when(userService.patchUser(any(), any())).thenReturn(UserDto.builder().build());
        mockMvc.perform(patch("/users/1").content(mapper.writeValueAsString(user)).header(BookingController.HEADER_USER_ID_FIELD, "1").characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    void getUserById() throws Exception {
        when(userService.getUserById(any())).thenReturn(UserDto.builder().build());
        mockMvc.perform(get("/users/1").header(BookingController.HEADER_USER_ID_FIELD, "1").characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    void getAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(UserDto.builder().build()));
        mockMvc.perform(get("/users").header(BookingController.HEADER_USER_ID_FIELD, "1").characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    void deleteUserById() throws Exception {
        mockMvc.perform(delete("/users/1").header(BookingController.HEADER_USER_ID_FIELD, "1")
                .characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        Mockito.verify(userService, Mockito.times(1)).deleteUserById(1L);
    }
}