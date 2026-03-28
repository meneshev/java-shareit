package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Set;

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

    public static ItemDto mapToDto(Item item, Set<CommentDto> comments) {
        ItemDto itemDto = mapToDto(item);
        itemDto.setComments(comments);
        return itemDto;
    }

    public static Item mapToEntity(CreateItemRequest request, User user) {
        Item item = new Item();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setIsAvailable(request.getAvailable());
        item.setOwner(user);
        return item;
    }

    public static Item mapToEntity(CreateItemRequest request, User user, ItemRequest itemRequest) {
        Item item = mapToEntity(request, user);
        item.setRequest(itemRequest);
        return item;
    }

    public static Item mapToEntity(ItemDto itemDto, User user) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setIsAvailable(itemDto.getAvailable());
        item.setOwner(user);
        return item;
    }

    public static Item mapToEntity(UpdateItemRequest request, User user, Long itemId) {
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(user);
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setIsAvailable(request.getAvailable());
        item.setRequest(null);
        return item;
    }

    public static Item mapToEntity(UpdateItemRequest request, User user, Long itemId, ItemRequest itemRequest) {
        Item item = mapToEntity(request, user, itemId);
        item.setRequest(itemRequest);
        return item;
    }
}
