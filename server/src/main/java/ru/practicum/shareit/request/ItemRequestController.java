package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForCreateDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Validated
@AllArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createItemRequest(@RequestBody ItemRequestForCreateDto itemRequestForCreateDto,
                                            @RequestHeader(value = BookingController.HEADER_USER_ID_FIELD) Long userId) {
        return itemRequestService.createItemRequest(itemRequestForCreateDto, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> findItemRequestForMe(@RequestHeader(value = BookingController.HEADER_USER_ID_FIELD) Long userId) {
        return itemRequestService.findItemRequestForMe(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> findAllRequest(@RequestParam(value = "from", required = false) Integer from,
                                               @RequestParam(value = "size", required = false) Integer size,
                                               @RequestHeader(value = BookingController.HEADER_USER_ID_FIELD) Long userId) {
        return itemRequestService.findAllRequest(from, size, userId);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto findItemRequestBuId(@PathVariable Long requestId,
                                              @RequestHeader(value = BookingController.HEADER_USER_ID_FIELD) Long userId) {
        return itemRequestService.findById(requestId, userId);
    }

}
























