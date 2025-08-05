package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import java.util.List;
import java.util.Optional;

public interface ItemStorage {

    Item create(Item item, Long userId);

    Item update(Item item);

    Boolean delete(Long itemId);

    Optional<Item> findById(Long itemId);

    List<Item> findAll();
}
