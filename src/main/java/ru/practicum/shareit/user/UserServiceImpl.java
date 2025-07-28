package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.exception.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(@Qualifier("inMemoryUserRepository") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto createUser(CreateUserRequest request) {
        User userToCreate = UserMapper.mapToEntity(request);
        userToCreate = userStorage.create(userToCreate);
        return UserMapper.mapToDto(userToCreate);
    }

    public UserDto updateUser(UpdateUserRequest request, Long userId) {
        UserDto oldUserData = getUserById(userId);
        if (request.isNameEmpty()) {
            request.setName(oldUserData.getName());
        }

        if (request.isEmailEmpty()) {
            request.setEmail(oldUserData.getEmail());
        }
        User userToUpdate = UserMapper.mapToEntity(request, userId);
        userToUpdate = userStorage.update(userToUpdate);
        return UserMapper.mapToDto(userToUpdate);
    }

    public Boolean deleteUser(Long id) {
        getUserById(id);
        return userStorage.delete(id);
    }

    public UserDto getUserById(Long id) {
        return userStorage.findById(id)
                .map(UserMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public List<UserDto> getAllUsers() {
        return userStorage.findAll().stream()
                .map(UserMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
