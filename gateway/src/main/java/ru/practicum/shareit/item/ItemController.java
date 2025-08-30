package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateCommentRequest;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{item-id}")
    @Cacheable(value = "single-item", key = "#userId + ':' + #itemId")
    public ResponseEntity<Object> getItem(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                          @PathVariable("item-id")  Long itemId) {
        log.info("Get item {}, userId={}",  itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    @Cacheable(value = "user-items", key = "#userId")
    public ResponseEntity<Object> getUserItems(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Get userId={} items", userId);
        return itemClient.getUserItems(userId);
    }

    @GetMapping("/search")
    @Cacheable(value = "search-item", key = "#userId + ':' + #searchString")
    public ResponseEntity<Object> searchItem(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                             @RequestParam("text") String searchString) {
        log.info("Search item, searchString={}, userId={}", searchString, userId);
        return itemClient.searchItem(userId, searchString);
    }

    @PostMapping
    @Caching(evict = {
            @CacheEvict(value = "user-items", key = "#userId"),
            @CacheEvict(value = "search-item", allEntries = true)
    })
    public ResponseEntity<Object> createItem(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody CreateItemRequest request) {
        log.info("Creating item {}, userId={}", request, userId);
        return itemClient.createItem(userId, request);
    }

    @PostMapping("/{item-id}/comment")
    @Caching(evict = {
            @CacheEvict(value = "user-items", key = "#userId"),
            @CacheEvict(value = "single-item", key = "#userId + ':' + #itemId")
    })
    public ResponseEntity<Object> createComment(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                @Valid @RequestBody CreateCommentRequest request,
                                                @PathVariable("item-id") Long itemId) {
        log.info("Creating comment {}, userId={}", request, itemId);
        return itemClient.createComment(userId, itemId, request);
    }

    @PatchMapping("/{item-id}")
    @Caching(
          evict = {
                @CacheEvict(value = "user-items", key = "#userId"),
                @CacheEvict(value = {
                        "search-item",
                        "user-requests",
                        "other-user-requests",
                        "single-request",
                        "single-booking",
                        "user-bookings",
                        "owner-bookings"
            }, allEntries = true),
         },
            put = @CachePut(value = "single-item", key = "#userId + ':' + #itemId")
    )
    public ResponseEntity<Object> updateItem(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody UpdateItemRequest request,
                                             @PathVariable("item-id")  Long itemId) {
        log.info("Updating item {}, userId={}", request, itemId);
        return itemClient.updateItem(userId, itemId, request);
    }

    @DeleteMapping("/{item-id}")
    @Caching(evict = {
            @CacheEvict(value = "user-items", key = "#userId"),
            @CacheEvict(value = "single-item", key = "#userId + ':' + #itemId"),
            @CacheEvict(value = {
                    "search-item",
                    "user-requests",
                    "other-user-requests",
                    "single-request",
                    "single-booking",
                    "user-bookings",
                    "owner-bookings"
            }, allEntries = true),
    })
    public ResponseEntity<Object> deleteItem(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                             @PathVariable("item-id")  Long itemId) {
        log.info("Deleting item {}, userId={}", itemId, userId);
        return itemClient.deleteItem(userId, itemId);
    }
}
