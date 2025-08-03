package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import java.util.List;

public interface ItemService {
    ItemDto createItem(CreateItemRequest request, Long userId);

    ItemDto updateItem(UpdateItemRequest request, Long userId, Long itemId);

    Boolean deleteItem(Long itemId, Long userId);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getItemsByUserId(Long userId);

    List<ItemDto> findItems(String searchString,  Long userId);
}
