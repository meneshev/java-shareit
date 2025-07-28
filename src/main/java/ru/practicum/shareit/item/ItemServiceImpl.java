package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserService userService;

    public ItemServiceImpl(@Qualifier("inMemoryItemRepository") ItemStorage itemStorage,
                           UserService userService) {
        this.itemStorage = itemStorage;
        this.userService = userService;
    }

    public ItemDto createItem(CreateItemRequest request, Long userId) {
        checkUserId(userId);
        Item itemToCreate = ItemMapper.mapToEntity(request, userId);
        itemToCreate = itemStorage.create(itemToCreate,  userId);
        return ItemMapper.mapToDto(itemToCreate);
    }

    @Override
    public ItemDto updateItem(UpdateItemRequest request, Long userId, Long itemId) {
        checkUserId(userId);

        ItemDto oldItemData = getItemById(itemId);
        validateItem(itemStorage.findById(itemId).get(), userId);

        if (request.isNameEmpty()) {
            request.setName(oldItemData.getName());
        }

        if (request.isDescriptionEmpty()) {
            request.setDescription(oldItemData.getDescription());
        }

        if (request.isAvailableEmpty()) {
            request.setAvailable(oldItemData.getAvailable());
        }
        Item itemToUpdate = ItemMapper.mapToEntity(request, userId, itemId);
        itemToUpdate = itemStorage.update(itemToUpdate);
        return ItemMapper.mapToDto(itemToUpdate);
    }

    @Override
    public Boolean deleteItem(Long itemId, Long userId) {
        checkUserId(userId);
        itemStorage.findById(itemId);
        validateItem(itemStorage.findById(itemId).get(), userId);
        return itemStorage.delete(itemId);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return itemStorage.findById(itemId)
                .map(ItemMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Item not found"));
    }

    @Override
    public List<ItemDto> getItemsByUserId(Long userId) {
        checkUserId(userId);
        return itemStorage.findAll().stream()
                .filter(item -> Objects.equals(item.getOwnerId(), userId))
                .map(ItemMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findItems(String searchString, Long userId) {
        checkUserId(userId);
        if (searchString == null || searchString.isEmpty()) {
            return List.of();
        }
        return itemStorage.findAll().stream()
                .filter(item -> item.getName().toLowerCase().contains(searchString.toLowerCase())
                        || item.getDescription().toLowerCase().contains(searchString.toLowerCase()))
                .filter(item -> item.getIsAvailable() != null && item.getIsAvailable())
                .map(ItemMapper::mapToDto)
                .collect(Collectors.toList());
    }

    private void checkUserId(Long userId) {
        if (userId == null) {
            throw new ValidationException("X-Sharer-User-Id is null");
        } else {
            userService.getUserById(userId);
        }
    }

    private void validateItem(Item item, Long userId) {
        if (!Objects.equals(item.getOwnerId(), userId)) {
            throw new ValidationException("Данная вещь принадлежит другому пользователю");
        }
    }
}
