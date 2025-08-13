package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import java.util.List;

public interface ItemService {
    ItemDto createItem(CreateItemRequest request, Long userId);

    ItemDto updateItem(UpdateItemRequest request, Long userId, Long itemId);

    void deleteItem(Long itemId, Long userId);

    ItemDto getItemDtoById(Long itemId, Long userId);

    Item getItemById(Long itemId);

    List<ItemDto> getItemsByUserId(Long userId);

    List<ItemDto> findItems(String searchString,  Long userId);

    ItemShort getShortItemById(Long itemId);

    void validateItem(Long itemId, Long userId);

    CommentDto createComment(CreateCommentRequest request, Long itemId, Long userId);
}
