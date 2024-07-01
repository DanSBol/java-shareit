package ru.practicum.shareit.request;

import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.User;
import java.util.List;

public class RequestMapper {
    public static RequestDto mapToRequestDto(Request request, List<ItemDto> itemsDto) {
        return new RequestDto.RequestDtoBuilder()
            .id(request.getId())
            .userId(request.getRequestor().getId())
            .description(request.getDescription())
            .created(request.getCreated().toString())
            .items(itemsDto)
            .build();
    }

    public static Request mapToRequest(RequestDto requestDto, User requestor) {
        Request request = new Request();
        request.setRequestor(requestor);
        request.setDescription(requestDto.getDescription());
        return request;
    }
}