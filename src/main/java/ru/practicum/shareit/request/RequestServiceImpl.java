package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public RequestDto addNewRequest(long userId, RequestDto requestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
        Request request = RequestMapper.mapToRequest(requestDto, user);
        request = requestRepository.saveAndFlush(request);
        return RequestMapper.mapToRequestDto(request);
    }

    @Override
    public RequestDto getRequest(long userId, long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Request not found."));
        return RequestMapper.mapToRequestDto(request);
    }

    @Override
    public List<RequestDto> getRequestsByOwner(long userId) {
        return requestRepository.getRequestsByOwner(userId);
    }

    @Override
    public Page<RequestDto> getRequestsByParam(long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        return requestRepository.getRequestsByParam(userId, pageable);
    }
}