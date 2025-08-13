package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public ItemDto createItem(CreateItemRequest request, Long userId) {
        userService.checkUserId(userId);
        User user = UserMapper.mapToEntity(userService.getUserDtoById(userId));
        Item itemToCreate = ItemMapper.mapToEntity(request, user);

        itemToCreate = itemRepository.save(itemToCreate);
        return ItemMapper.mapToDto(itemToCreate);
    }

    @Override
    @Transactional
    public ItemDto updateItem(UpdateItemRequest request, Long userId, Long itemId) {
        userService.checkUserId(userId);

        ItemDto oldItemData = getItemDtoById(itemId, userId);
        validateItem(itemId, userId);

        if (request.isNameEmpty()) {
            request.setName(oldItemData.getName());
        }

        if (request.isDescriptionEmpty()) {
            request.setDescription(oldItemData.getDescription());
        }

        if (request.isAvailableEmpty()) {
            request.setAvailable(oldItemData.getAvailable());
        }

        User user = UserMapper.mapToEntity(userService.getUserDtoById(userId));
        Item itemToUpdate = ItemMapper.mapToEntity(request, user, itemId);
        itemToUpdate = itemRepository.save(itemToUpdate);
        return ItemMapper.mapToDto(itemToUpdate);
    }

    @Override
    @Transactional
    public void deleteItem(Long itemId, Long userId) {
        userService.checkUserId(userId);
        validateItem(itemId, userId);
        itemRepository.deleteById(itemId);
    }

    @Override
    public ItemDto getItemDtoById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new NotFoundException("Item not found"));

        Set<CommentDto> comments = commentRepository.findByItem_Id(itemId).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toSet());

        ItemDto itemDto = ItemMapper.mapToDto(item, comments);

        if (item.getOwner().getId().equals(userId)) {
            itemDto.setLastBooking(bookingRepository.getLastEndDateByItemId(itemId));
            itemDto.setNextBooking(bookingRepository.getNextStartDateByItemId(itemId));
        }

        return itemDto;
    }

    @Override
    public List<ItemDto> getItemsByUserId(Long userId) {
        userService.checkUserId(userId);
        User user = UserMapper.mapToEntity(userService.getUserDtoById(userId));

        List<Item> items = itemRepository.findAllByOwner_Id(userId);

        return items.stream()
                .map(item -> {
                    Set<CommentDto> comments = commentRepository.findByItem_Id(item.getId()).stream()
                            .map(CommentMapper::toDto)
                            .collect(Collectors.toSet());

                    return ItemMapper.mapToDto(item, comments);
                })
                .toList();
    }

    @Override
    public List<ItemDto> findItems(String searchString, Long userId) {
        userService.checkUserId(userId);
        if (searchString == null || searchString.isEmpty()) {
            return List.of();
        }

        return itemRepository.findBySearchString(
                searchString, searchString,true).stream()
                .map(ItemMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemShort getShortItemById(Long itemId) {
        return itemRepository.findShort(itemId);
    }

    @Override
    public void validateItem(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).get();
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new ValidationException("Данная вещь принадлежит другому пользователю");
        }
    }

    @Override
    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));
    }

    @Override
    @Transactional
    public CommentDto createComment(CreateCommentRequest request, Long itemId, Long userId) {
        bookingRepository.findByBookerId(userId).stream()
                .filter(bookingDto -> bookingDto.getItem().id().equals(itemId)
                        && bookingDto.getStatus().equals(BookingStatus.APPROVED)
                        && LocalDateTime.now().isAfter(bookingDto.getEnd()))
                .findAny()
                .orElseThrow(() -> new ValidationException("Вы не брали данную вещь в аренду"));

        Item item = itemRepository.findById(itemId).get();
        User user = UserMapper.mapToEntity(userService.getUserDtoById(userId));
        Comment commentToSave = CommentMapper.toEntity(request, user, item);
        return CommentMapper.toDto(commentRepository.save(commentToSave));
    }
}
