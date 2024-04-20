package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.*;

@Component
@Primary
@RequiredArgsConstructor
public class ItemRequestImpl implements ItemRequestService {
    private final Map<Long, ItemRequest> itemRequests = new HashMap<>();

    private int id = 0;

    @Override
    public ItemRequest addItemRequest(ItemRequest itemRequest) {
        itemRequest.setId(++id);
        itemRequests.put(itemRequest.getId(), itemRequest);
        return itemRequest;
    }

    @Override
    public Collection<ItemRequest> getItemRequest() {
        return itemRequests.values();
    }
}