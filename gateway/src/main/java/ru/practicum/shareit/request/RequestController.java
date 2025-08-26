package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestReqDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @GetMapping
    @Cacheable(value = "user-requests", key = "#userId")
    public ResponseEntity<Object> getUserRequests(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Getting requests for userId={}", userId);
        return requestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    @Cacheable(value = "other-user-requests", key = "#userId")
    public ResponseEntity<Object> getOtherUsersRequests(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Getting requests for other users, userId={}", userId);
        return requestClient.getOtherUsersRequests(userId);
    }

    @GetMapping("/{request-id}")
    @Cacheable(value = "single-request", key = "#requestId")
    public ResponseEntity<Object> getRequest(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                             @PathVariable("request-id")  Long requestId) {
        log.info("Getting requestId={}, userId={}", requestId, userId);
        return requestClient.getRequest(userId, requestId);
    }

    @PostMapping
    @CacheEvict(value = {
            "user-requests",
            "other-user-requests"
    }, allEntries = true)
    public ResponseEntity<Object> createRequest(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                @RequestBody @Valid CreateItemRequestReqDto createRequest) {
        log.info("Creating request {}, userId={}", createRequest, userId);
        return requestClient.createRequest(userId, createRequest);
    }
}
