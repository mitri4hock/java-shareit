package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForCreateDto;
import ru.practicum.shareit.request.model.ItemRequest;

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
    /**

     * GET /requests — получить список своих запросов вместе с данными об ответах на них. Для каждого запроса должны
     * указываться описание, дата и время создания и список ответов в формате: id вещи, название, id владельца.
     * Так в дальнейшем, используя указанные id вещей, можно будет получить подробную информацию о каждой вещи.
     * Запросы должны возвращаться в отсортированном порядке от более новых к более старым.
     *
     * GET /requests/all?from={from}&size={size} — получить список запросов, созданных другими пользователями.
     * С помощью этого эндпоинта пользователи смогут просматривать существующие запросы, на которые они могли бы ответить.
     * Запросы сортируются по дате создания: от более новых к более старым. Результаты должны возвращаться постранично.
     * Для этого нужно передать два параметра: from — индекс первого элемента, начиная с 0, и size — количество элементов для отображения.
     *
     * GET /requests/{requestId} — получить данные об одном конкретном запросе вместе с данными об ответах на него в
     * том же формате, что и в эндпоинте GET /requests. Посмотреть данные об отдельном запросе может любой пользователь.
     */

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createItemRequest (@RequestBody @Valid ItemRequestForCreateDto itemRequestForCreateDto,
                                             @RequestHeader(value = headerUserIdField) @NotNull Long userId){
        return itemRequestService.createItemRequest(itemRequestForCreateDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> findItemRequestForMe (@RequestHeader(value = headerUserIdField) @NotNull Long userId){
        return itemRequestService.findItemRequestForMe(userId);
    }

}
























