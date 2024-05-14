package ru.practicum.shareit.request;

import ru.practicum.shareit.user.User;
import java.util.List;
import java.util.stream.Collectors;

public class RequestMapper {
    public static RequestDto mapToRequestDto(Request request) {
        return new RequestDto.RequestDtoBuilder()
            .id(request.getId())
            .userId(request.getRequestor().getId())
            .description(request.getDescription())
            .created(request.getCreated())
            .build();
    }

    public static Request mapToRequest(RequestDto requestDto, User requester) {
        return new Request.RequestBuilder()
            .requestor(requester)
            .description(requestDto.getDescription())
            .created(requestDto.getCreated())
            .build();
    }

    public static List<RequestDto> mapToRequestDto(List<Request> requests) {
        return requests.stream()
            .map(RequestMapper::mapToRequestDto)
            .collect(Collectors.toList());
    }
}