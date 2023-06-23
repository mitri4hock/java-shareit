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
     *
     * Добавим ещё одну полезную опцию в ваше приложение, чтобы пользователи могли отвечать на запросы друг друга.
     * Для этого при создании вещи должна быть возможность указать id запроса, в ответ на который создаётся нужная вещь.
     * Добавьте поле requestId в тело запроса POST /items. Обратите внимание, что должна сохраниться возможность
     * добавить вещь и без указания requestId.
     *
     *
     * Теперь вернёмся к улучшению, о котором мы упомянули ранее. Вы уже используете в запросе GET /requests/all
     * пагинацию, поскольку запросов может быть очень много.
     * Пользователи уже жалуются, что запросы возвращают слишком много данных и с ними невозможно работать.
     * Эта проблема возникает при просмотре бронирований и особенно при просмотре вещей. Поэтому, чтобы приложение
     * было комфортным для пользователей, а также быстро работало, вам предстоит добавить пагинацию в эндпоинты
     * GET /items, GET /items/search, GET /bookings и GET /bookings/owner.
     * Параметры будут такими же, как и для эндпоинта на получение запросов вещей: номер первой записи и
     * желаемое количество элементов для отображения.
     *
     * Для реализации пагинации используйте возможности, предоставляемые JpaRepository . Вам нужно определить в
     * интерфейсе репозитория метод поиска, аналогичный тому, который вы использовали ранее, но принимающий в
     * качестве параметра также объект Pageable . Например, для поиска вещи ранее использовался метод List<Item>
     * findByOwnerId , создайте метод Page<Item> findByOwnerId(Long ownerId, Pageable pageable) . Тогда всё остальное для
     * реализации пагинации на уровне базы данных для вас сделает Spring.
     * Вам нужно будет только изменить вызов к данному методу, передавая в качестве дополнительного параметра
     * описание требуемой страницы. Для этого используйте метод PageRequest.of(page, size, sort) . Обратите внимание,
     * что вам нужно будет преобразовать параметры, передаваемые пользователем, — start и size — к параметрам,
     * требуемым Spring, — page и тот же size .
     *
     *
     *  public void checkUsers(){
     *                 // сначала создаём описание сортировки по полю id
     *         Sort sortById = Sort.by(Sort.Direction.ASC, "id");
     *                 // затем создаём описание первой "страницы" размером 32 элемента
     *         Pageable page = PageRequest.of(0, 32, sortById);
     *         do {
     *                         // запрашиваем у базы данных страницу с данными
     *             Page<User> userPage = repository.findAll(page);
     *                         // результат запроса получаем с помощью метода getContent()
     *             userPage.getContent().forEach(user -> {
     *                 // проверяем пользователей
     *             });
     *                         // для типа Page проверяем, существует ли следующая страница
     *             if(userPage.hasNext()){
     *                                 // если следующая страница существует, создаём её описание, чтобы запросить на следующей итерации цикла
     *                 page = PageRequest.of(userPage.getNumber() + 1, userPage.getSize(), userPage.getSort()); // или для простоты -- userPage.nextOrLastPageable()
     *             } else {
     *                 page = null;
     *             }
     *         } while (page != null);
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

    @GetMapping("/all") //?from={from}&size={size}
    public List<ItemRequestDto> findAllRequest (@RequestParam(value = "from", required = false) Integer from,
                                                @RequestParam(value = "size", required = false) Integer size){
        return itemRequestService.findAllRequest(from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findItemRequestBuId (@PathVariable Long requestId){
        return itemRequestService.findById(requestId);
    }

}
























