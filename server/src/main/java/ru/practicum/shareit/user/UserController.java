package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto save(@RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @PatchMapping("/{user-id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto update(@RequestBody UpdateUserRequest request,
                          @PathVariable("user-id") Long userId) {
        return userService.updateUser(request, userId);
    }

    @DeleteMapping("/{user-id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable("user-id") Long userId) {
        userService.deleteUser(userId);
        Map<String, String> response = Map.of("message", "User has been deleted");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{user-id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserById(@PathVariable("user-id") Long userId) {
        return userService.getUserDtoById(userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }
}
