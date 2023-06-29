package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForCreateDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
    @Mock
    private ItemRequestService itemRequestService;
    @InjectMocks
    private ItemRequestController itemRequestController;
    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();
    private BookingDto returnObject;

    @BeforeEach
    void setUp() {
        mapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .build();

        returnObject = BookingDto.builder()
                .id(1L)
                .build();
    }

    @Test
    void createItemRequest() throws Exception {
        ItemRequestForCreateDto itemRequestForCreateDto = new ItemRequestForCreateDto();
        itemRequestForCreateDto.setDescription("test");

        when(itemRequestService.createItemRequest(any(), any()))
                .thenReturn(ItemRequestDto.builder().build());
        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestForCreateDto))
                        .header(BookingController.HEADER_USER_ID_FIELD, "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void findItemRequestForMe() throws Exception {
        when(itemRequestService.findItemRequestForMe(any()))
                .thenReturn(List.of(ItemRequestDto.builder().build()));

        mockMvc.perform(get("/requests")
                        .header(BookingController.HEADER_USER_ID_FIELD, "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findAllRequest() throws Exception {
        when(itemRequestService.findAllRequest(any(), any(), any()))
                .thenReturn(List.of(ItemRequestDto.builder().build()));

        mockMvc.perform(get("/requests/all?from=1&size=1")
                        .header(BookingController.HEADER_USER_ID_FIELD, "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findItemRequestBuId() throws Exception {
        when(itemRequestService.findById(any(), any()))
                .thenReturn(ItemRequestDto.builder().build());

        mockMvc.perform(get("/requests/1")
                        .header(BookingController.HEADER_USER_ID_FIELD, "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}