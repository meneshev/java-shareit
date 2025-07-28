package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {
    public static ItemDto mapToDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getIsAvailable());
        return itemDto;
    }

    public static Item mapToEntity(CreateItemRequest request, Long userId) {
        Item item = new Item();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setIsAvailable(request.getAvailable());
        item.setOwnerId(userId);
        return item;
    }

    public static Item mapToEntity(UpdateItemRequest request, Long userId, Long itemId) {
        Item item = new Item();
        item.setId(itemId);
        item.setOwnerId(userId);
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setIsAvailable(request.getAvailable());
        return item;
    }
}
