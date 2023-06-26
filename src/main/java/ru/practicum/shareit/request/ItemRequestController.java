package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForCreateDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Validated
@AllArgsConstructor
public class ItemRequestController {

    private final String headerUserIdField = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createItemRequest(@RequestBody @Valid ItemRequestForCreateDto itemRequestForCreateDto,
                                            @RequestHeader(value = headerUserIdField) @NotNull Long userId) {
        return itemRequestService.createItemRequest(itemRequestForCreateDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> findItemRequestForMe(@RequestHeader(value = headerUserIdField) @NotNull Long userId) {
        return itemRequestService.findItemRequestForMe(userId);
    }

    @GetMapping("/all") //?from={from}&size={size}
    public List<ItemRequestDto> findAllRequest(@RequestParam(value = "from", required = false) Integer from,
                                               @RequestParam(value = "size", required = false) Integer size,
                                               @RequestHeader(value = headerUserIdField) @NotNull Long userId) {
        return itemRequestService.findAllRequest(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findItemRequestBuId(@PathVariable Long requestId,
                                              @RequestHeader(value = headerUserIdField) @NotNull Long userId) {
        return itemRequestService.findById(requestId, userId);
    }

}
























