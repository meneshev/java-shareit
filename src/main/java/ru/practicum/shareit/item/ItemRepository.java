package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import java.util.*;

@Repository
@Qualifier("inMemoryItemRepository")
public class ItemRepository implements ItemStorage {
    private final Map<Long, Item> itemsMap = new HashMap<>();
    private static Long nextId = 1L;

    private Long getNextId() {
        return nextId++;
    }

    @Override
    public Item create(Item item, Long userId) {
        item.setId(getNextId());
        item.setOwnerId(userId);
        itemsMap.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        itemsMap.put(item.getId(), item);
        return item;
    }

    @Override
    public Boolean delete(Long itemId) {
        return itemsMap.remove(itemId) != null;
    }

    @Override
    public Optional<Item> findById(Long itemId) {
        return Optional.ofNullable(itemsMap.get(itemId));
    }

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(itemsMap.values());
    }

}
