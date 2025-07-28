package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(CreateUserRequest request);

    UserDto updateUser(UpdateUserRequest request, Long userId);

    Boolean deleteUser(Long id);

    UserDto getUserById(Long id);

    List<UserDto> getAllUsers();
}
