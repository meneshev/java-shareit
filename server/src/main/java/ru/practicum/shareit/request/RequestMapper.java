package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemAnswerDto;
import ru.practicum.shareit.request.dto.CreateItemRequestReqDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequestMapper {

    public static RequestDto mapToDto(ItemRequest itemRequest) {
        RequestDto requestDto = new RequestDto();
        requestDto.setId(itemRequest.getId());
        requestDto.setDescription(itemRequest.getDescription());
        requestDto.setCreated(itemRequest.getCreated());
        return requestDto;
    }

    public static RequestDto mapToDto(ItemRequest itemRequest, List<ItemAnswerDto> answers) {
        RequestDto requestDto = mapToDto(itemRequest);
        requestDto.setItems(answers);
        return requestDto;
    }

    public static ItemRequest mapToEntity(CreateItemRequestReqDto requestDto, User user) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(requestDto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);
        return itemRequest;
    }
}
