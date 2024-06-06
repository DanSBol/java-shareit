package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public RequestDto addRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @Valid @RequestBody RequestDto requestDto) {
        return requestService.addNewRequest(userId, requestDto);
    }

    @GetMapping("/{requestId}")
    public RequestDto getRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long requestId) {
        return requestService.getRequest(userId, requestId);
    }

    @GetMapping
    public List<RequestDto> getRequestsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getRequestsByOwner(userId);
    }

    @GetMapping("/all")
    public Page<RequestDto> getRequestsByParam(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam Integer from,
                                               @RequestParam Integer size) {
        return requestService.getRequestsByParam(userId, from, size);
    }
}