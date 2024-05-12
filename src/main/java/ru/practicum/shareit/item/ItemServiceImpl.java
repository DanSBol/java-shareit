package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDto addNewItem(long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
        Item item = ItemMapper.mapToItem(itemDto, user, null);
        item = itemRepository.saveAndFlush(item);
        return ItemMapper.mapToItemDto(item, null, null, new HashSet<>());
    }

    @Transactional
    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found."));
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Item not found.");
        }
        if (userId != item.getOwner().getId()) {
            throw new NotFoundException("This item has another owner.");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        item = itemRepository.saveAndFlush(item);
        List<Booking> bookingsPast = bookingRepository.getBookingOnePast(item);
        BookingShotDto bookingPast = bookingsPast.isEmpty() ? null :
                BookingMapper.mapToBookingShotDto(bookingsPast.get(0));
        List<Booking> bookingsNext = bookingRepository.getBookingOneFutureAllStatuses(item);
        BookingShotDto bookingNext = bookingsNext.isEmpty() ? null :
                BookingMapper.mapToBookingShotDto(bookingsNext.get(0));
        List<Comment> comments = commentRepository.getCommentsForItem(item.getId());
        Set<CommentDto> commentsDto = new HashSet<CommentDto>(CommentMapper.mapToCommentsDto(comments));
        return ItemMapper.mapToItemDto(item, bookingPast, bookingNext, commentsDto);
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found."));
        if (userId != item.getOwner().getId()) {
            throw new NotFoundException("This item has another owner.");
        }
        itemRepository.deleteById(itemId);
    }

    @Override
    public ItemDto getItem(long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found."));
        List<Booking> bookingsPast = bookingRepository.getBookingOnePast(item);
        BookingShotDto bookingPast = bookingsPast.isEmpty() ? null :
                BookingMapper.mapToBookingShotDto(bookingsPast.get(0));
        List<Booking> bookingsNext = bookingRepository.getBookingOneFutureApproved(item);
        BookingShotDto bookingNext = bookingsNext.isEmpty() ? null :
                BookingMapper.mapToBookingShotDto(bookingsNext.get(0));
        List<Comment> comments = commentRepository.getCommentsForItem(item.getId());
        Set<CommentDto> commentsDto = new HashSet<CommentDto>(CommentMapper.mapToCommentsDto(comments));
        return ItemMapper.mapToItemDto(item, userId == item.getOwner().getId() ? bookingPast : null,
                userId == item.getOwner().getId() ? bookingNext : null, commentsDto);
    }

    @Override
    public List<ItemDto> getItemsByOwner(long userId) {
        List<Item> items = itemRepository.getItemsByOwner(userId);
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            List<Booking> bookingsPast = bookingRepository.getBookingOnePast(item);
            BookingShotDto bookingPast = bookingsPast.isEmpty() ? null :
                    BookingMapper.mapToBookingShotDto(bookingsPast.get(0));
            List<Booking> bookingsNext = bookingRepository.getBookingOneFutureAllStatuses(item);
            BookingShotDto bookingNext = bookingsNext.isEmpty() ? null :
                    BookingMapper.mapToBookingShotDto(bookingsNext.get(0));
            List<Comment> comments = commentRepository.getCommentsForItem(item.getId());
            Set<CommentDto> commentsDto = new HashSet<CommentDto>(CommentMapper.mapToCommentsDto(comments));
            itemsDto.add(ItemMapper.mapToItemDto(item, bookingPast, bookingNext, commentsDto));
        }
        return itemsDto;
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isEmpty()) {
            return new ArrayList<ItemDto>();
        } else {
            List<Item> items = itemRepository.findByAvailableAndDescriptionContainingIgnoreCaseOrderById(true,
                    text);
            List<ItemDto> itemsDto = new ArrayList<>();
            for (Item item : items) {
                List<Booking> bookingsPast = bookingRepository.getBookingOnePast(item);
                BookingShotDto bookingPast = bookingsPast.isEmpty() ? null :
                        BookingMapper.mapToBookingShotDto(bookingsPast.get(0));
                List<Booking> bookingsNext = bookingRepository.getBookingOneFutureApproved(item);
                BookingShotDto bookingNext = bookingsNext.isEmpty() ? null :
                        BookingMapper.mapToBookingShotDto(bookingsNext.get(0));
                itemsDto.add(ItemMapper.mapToItemDto(item, bookingPast, bookingNext, null));
            }
            return itemsDto;
        }
    }

    @Transactional
    @Override
    public CommentDto addNewComment(long userId, long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found."));
        List<Booking> bookings = bookingRepository.getBookingByItemAndBooker(item, user);
        if (bookings.isEmpty()) {
            throw new BadRequestException("No one booking before.");
        }
        if (commentDto.getText().isEmpty()) {
            throw new BadRequestException("Empty comment.");
        }
        Comment comment = CommentMapper.mapToComment(commentDto, user, item);
        comment = commentRepository.saveAndFlush(comment);
        return CommentMapper.mapToCommentDto(comment);
    }
}