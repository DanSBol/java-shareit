package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping()
    public ItemRequest addItemRequest(@RequestHeader("X-ShareIt-User-Id") Long userId,
                                          @RequestBody ItemRequest itemRequest) {
        return itemRequestService.addItemRequest(itemRequest);
    }

    @GetMapping()
    public Collection<ItemRequest> getItemRequest(@RequestHeader("X-ShareIt-User-Id") Long userId) {
        return itemRequestService.getItemRequest();
    }
}

