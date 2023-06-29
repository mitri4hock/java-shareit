package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForCreate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    @Mock
    private BookingService bookingService;
    @InjectMocks
    private BookingController bookingController;
    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();
    private BookingDto returnObject;

    @BeforeEach
    void setUp() {
        mapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        returnObject = BookingDto.builder()
                .id(1L)
                .build();
    }


    @Test
    void createBooking() throws Exception {
        BookingDtoForCreate bookingDtoForCreate = new BookingDtoForCreate();
        bookingDtoForCreate.setItemId(1L);
        bookingDtoForCreate.setStart(LocalDateTime.now().plusDays(1L));
        bookingDtoForCreate.setEnd(LocalDateTime.now().plusDays(2L));
        when(bookingService.saveBooking(any(), any()))
                .thenReturn(returnObject);
        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoForCreate))
                        .header(BookingController.HEADER_USER_ID_FIELD, "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(returnObject.getId()))
                .andDo(MockMvcResultHandlers.print());
        bookingDtoForCreate.setItemId(null);
        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoForCreate))
                        .header(BookingController.HEADER_USER_ID_FIELD, "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void verificationBooking() throws Exception {
        when(bookingService.updateApproved(any(), any(), any()))
                .thenReturn(returnObject);
        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header(BookingController.HEADER_USER_ID_FIELD, "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getBooking() throws Exception {
        when(bookingService.findBookingById(any(), any()))
                .thenReturn(returnObject);
        mockMvc.perform(get("/bookings/1")
                        .header(BookingController.HEADER_USER_ID_FIELD, "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findAllBookingWithStatus() throws Exception {
        when(bookingService.findAllBookingWithStatus(any(), any(), any(), any()))
                .thenReturn(List.of(returnObject));
        mockMvc.perform(get("/bookings?state=All,from=0,size=1")
                        .header(BookingController.HEADER_USER_ID_FIELD, "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findAllBookingForUserWithStatus() throws Exception {
        when(bookingService.findAllBookingForUserWithStatus(any(), any(), any(), any()))
                .thenReturn(List.of(returnObject));
        mockMvc.perform(get("/bookings/owner?state=ALL,from=0,size=1")
                        .header(BookingController.HEADER_USER_ID_FIELD, "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}