package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User create(User user);
    User update(User user);
    Boolean delete(Long userId);
    Optional<User> findById(Long userId);
    List<User> findAll();
}
