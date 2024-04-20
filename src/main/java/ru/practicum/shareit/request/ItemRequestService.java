package ru.practicum.shareit.request;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequest addItemRequest(ItemRequest itemRequest);

    Collection<ItemRequest> getItemRequest();
}