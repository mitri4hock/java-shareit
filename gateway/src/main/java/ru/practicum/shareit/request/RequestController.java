package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestForCreateDto;
import ru.practicum.shareit.util.Constants;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItemRequest(@RequestBody @Valid ItemRequestForCreateDto itemRequestForCreateDto,
                                                    @RequestHeader(Constants.HEADER_USER_ID_FIELD) @NotNull Long userId) {
        return requestClient.createItemRequest(userId, itemRequestForCreateDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> findItemRequestForMe(@RequestHeader(Constants.HEADER_USER_ID_FIELD) @NotNull Long userId) {
        return requestClient.findItemRequestForMe(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> findAllRequest(@RequestParam(value = "from", required = false) @PositiveOrZero Integer from,
                                                 @RequestParam(value = "size", required = false) @Positive Integer size,
                                                 @RequestHeader(Constants.HEADER_USER_ID_FIELD) @NotNull Long userId) {
        return requestClient.findAllRequest(userId, from, size);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> findItemRequestBuId(@PathVariable Long requestId,
                                                      @RequestHeader(Constants.HEADER_USER_ID_FIELD) @NotNull Long userId) {
        return requestClient.findById(userId, requestId);
    }
}
