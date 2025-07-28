package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.exception.ValidationException;
import java.util.*;

@Repository
@Qualifier("inMemoryUserRepository")
public class UserRepository implements UserStorage {
    private final Map<Long, User> usersMap = new HashMap<>();
    private final Map<String, Long> emails = new HashMap<>();

    private static Long nextId = 1L;

    private Long getNextId() {
        return nextId++;
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        validateUser(user);
        emails.put(user.getEmail(), user.getId());
        usersMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        validateUser(user);
        if (!emails.containsKey(user.getEmail())) {
            emails.remove(usersMap.get(user.getId()).getEmail());
        }
        emails.put(user.getEmail(), user.getId());
        usersMap.put(user.getId(), user);
        return user;
    }

    @Override
    public Boolean delete(Long userId) {
        emails.remove(findById(userId).get().getEmail());
        return usersMap.remove(userId) != null;
    }

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(usersMap.get(userId));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(usersMap.values());
    }

    private void validateUser(User user) {
        if (emails.containsKey(user.getEmail())
                && (!emails.get(user.getEmail()).equals(user.getId()))) {
            throw new ValidationException("Такой email уже принадлежит другому пользователю");
        }
    }
}
