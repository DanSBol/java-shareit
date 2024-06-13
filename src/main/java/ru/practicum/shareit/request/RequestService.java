package ru.practicum.shareit.request;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface RequestService {

    @Transactional
    RequestDto addRequest(long userId, RequestDto requestDto);

    RequestDto getRequest(long userId, long requestId);

    List<RequestDto> getRequestsByOwner(long userId);

    List<RequestDto> getRequestsByParam(long userId, int from, int size);
}