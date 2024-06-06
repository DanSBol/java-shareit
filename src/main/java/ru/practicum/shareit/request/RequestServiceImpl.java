package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.*;
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
        return RequestMapper.mapToRequestDto(request, null);
    }

    @Override
    public RequestDto getRequest(long userId, long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
        Request request = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Request not found."));
        List<Item> items = itemRepository.findByRequestId(requestId);
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(ItemMapper.mapToItemDto(item, null, null, null));
        }
        return RequestMapper.mapToRequestDto(request, itemsDto);
    }

    @Override
    public List<RequestDto> getRequestsByOwner(long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
        List<Request> requests = requestRepository.getRequestsByOwner(userId);
        List<RequestDto> requestsDto = new ArrayList<>();
        for (Request request : requests) {
            List<Item> items = itemRepository.findByRequestId(request.getId());
            List<ItemDto> itemsDto = new ArrayList<>();
            for (Item item : items) {
                itemsDto.add(ItemMapper.mapToItemDto(item, null, null, null));
            }
            requestsDto.add(RequestMapper.mapToRequestDto(request, itemsDto));
        }
        return requestsDto;
    }

    @Override
    public List<RequestDto> getRequestsByParam(long userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
        Pageable pageable = PageRequest.of(from, size);
        List<Request> requests = requestRepository.getRequestsByParam(userId, pageable).getContent();
        List<RequestDto> requestsDto = new ArrayList<>();
        for (Request request : requests) {
            List<Item> items = itemRepository.findByRequestId(request.getId());
            List<ItemDto> itemsDto = new ArrayList<>();
            for (Item item : items) {
                itemsDto.add(ItemMapper.mapToItemDto(item, null, null, null));
            }
            requestsDto.add(RequestMapper.mapToRequestDto(request, itemsDto));
        }
        return requestsDto;
    }
}