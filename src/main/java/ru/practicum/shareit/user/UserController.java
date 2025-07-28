package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto save(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @PatchMapping("/{user-id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto update(@Valid @RequestBody UpdateUserRequest request, @PathVariable("user-id") Long userId) {
        return userService.updateUser(request, userId);
    }

    @DeleteMapping("/{user-id}")
    public ResponseEntity<String> deleteUser(@PathVariable("user-id") Long userId) {
        if (userService.deleteUser(userId)) {
            return new ResponseEntity<>("User has been deleted", HttpStatus.OK);
        } else  {
            return new ResponseEntity<>("Error during deleting", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{user-id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserById(@PathVariable("user-id") Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }
}
