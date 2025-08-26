package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.exception.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        User createdUser = userRepository.save(UserMapper.mapToEntity(request));
        return UserMapper.mapToDto(createdUser);
    }

    @Override
    @Transactional
    public UserDto updateUser(UpdateUserRequest request, Long userId) {
        UserDto oldUserData = getUserDtoById(userId);
        if (request.isNameEmpty()) {
            request.setName(oldUserData.getName());
        }

        if (request.isEmailEmpty()) {
            request.setEmail(oldUserData.getEmail());
        }
        User userToUpdate = UserMapper.mapToEntity(request, userId);
        userToUpdate = userRepository.save(userToUpdate);
        return UserMapper.mapToDto(userToUpdate);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User userToDelete = UserMapper.mapToEntity(getUserDtoById(id));
        userRepository.delete(userToDelete);
    }

    @Override
    public UserDto getUserDtoById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void checkUserId(Long userId) {
        if (userId == null) {
            throw new ValidationException("X-Sharer-User-Id is null");
        } else {
            getUserDtoById(userId);
        }
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
