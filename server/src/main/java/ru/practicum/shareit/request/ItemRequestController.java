package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestReqDto;
import ru.practicum.shareit.request.dto.RequestDto;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> getUserRequests(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemRequestService.getRequestsByUser(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> getOtherUsersRequests(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemRequestService.getRequestsByOtherUsers(userId);
    }

    @GetMapping("/{request-id}")
    @ResponseStatus(HttpStatus.OK)
    public RequestDto getRequestById(@PathVariable("request-id")  Long requestId,
                                     @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemRequestService.getRequestById(requestId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@RequestBody CreateItemRequestReqDto createRequest,
                                    @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemRequestService.saveRequest(createRequest, userId);
    }
}

