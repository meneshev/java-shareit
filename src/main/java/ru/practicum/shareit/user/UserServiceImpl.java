package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.exception.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userRepository;

    public UserDto createUser(CreateUserRequest request) {
        User userToCreate = UserMapper.mapToEntity(request);
        userToCreate = userRepository.create(userToCreate);
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
        userToUpdate = userRepository.update(userToUpdate);
        return UserMapper.mapToDto(userToUpdate);
    }

    public Boolean deleteUser(Long id) {
        getUserById(id);
        return userRepository.delete(id);
    }

    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
