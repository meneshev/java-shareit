package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    @Cacheable("all-users")
    public ResponseEntity<Object> getUsers() {
        log.info("Get users");
        return userClient.getUsers();
    }

    @GetMapping("/{user-id}")
    @Cacheable(value = "single-user", key = "#userId")
    public ResponseEntity<Object> getUser(@PathVariable("user-id") Long userId) {
        log.info("Get userId={}", userId);
        return userClient.getUser(userId);
    }

    @PostMapping
    @CacheEvict(value = "all-users", allEntries = true)
    public ResponseEntity<Object> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        log.info("Create user {}", createUserRequest);
        return userClient.createUser(createUserRequest);
    }

    @PatchMapping("/{user-id}")
    @Caching(
            evict = @CacheEvict(value = "all-users", allEntries = true),
            put = @CachePut(value = "single-user", key = "#userId")
    )
    public ResponseEntity<Object> updateUser(@PathVariable("user-id") Long userId,
                                             @RequestBody @Valid UpdateUserRequest updateUserRequest) {
        log.info("Update user {}, userId={}", updateUserRequest, userId);
        return userClient.updateUser(userId, updateUserRequest);
    }

    @DeleteMapping("/{user-id}")
    @Caching(
            evict = {
                    @CacheEvict(value = "all-users", allEntries = true),
                    @CacheEvict(value = "single-user", key = "#userId")
            }
    )
    public ResponseEntity<Object> deleteUser(@PathVariable("user-id") Long userId) {
        log.info("Delete userId={}", userId);
        return userClient.deleteUser(userId);
    }
}
