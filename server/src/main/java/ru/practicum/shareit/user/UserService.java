package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import java.util.List;

public interface UserService {
    UserDto createUser(CreateUserRequest request);

    UserDto updateUser(UpdateUserRequest request, Long userId);

    void deleteUser(Long id);

    UserDto getUserDtoById(Long id);

    List<UserDto> getAllUsers();

    void checkUserId(Long userId);

    User getUserById(Long userId);
}
