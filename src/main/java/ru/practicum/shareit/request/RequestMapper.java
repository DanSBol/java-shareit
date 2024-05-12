package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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

    public static List<RequestDto> mapToRequestDto(Iterable<Request> requests) {
        List<RequestDto> result = new ArrayList<>();
        for (Request request : requests) {
            result.add(mapToRequestDto(request));
        }
        return result;
    }
}