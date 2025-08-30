package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.CreateItemRequestReqDto;
import ru.practicum.shareit.item.dto.ItemAnswerDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    @Transactional
    public RequestDto saveRequest(CreateItemRequestReqDto createRequest, Long userId) {
        userService.checkUserId(userId);
        User user = userService.getUserById(userId);
        ItemRequest itemRequest = RequestMapper.mapToEntity(createRequest, user);
        return RequestMapper.mapToDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<RequestDto> getRequestsByUser(Long userId) {
        userService.checkUserId(userId);
        return itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId).stream()
                .map(itemRequest -> {
                    List<ItemAnswerDto> answers = itemRepository.findAllByRequestId(itemRequest.getId());
                    return RequestMapper.mapToDto(itemRequest, answers);
                })
                .toList();
    }

    @Override
    public List<RequestDto> getRequestsByOtherUsers(Long userId) {
        userService.checkUserId(userId);
        return itemRequestRepository.findAllForOtherUsers(userId).stream()
                .map(itemRequest -> {
                    List<ItemAnswerDto> answers = itemRepository.findAllByRequestId(itemRequest.getId());
                    return RequestMapper.mapToDto(itemRequest, answers);
                })
                .toList();
    }

    @Override
    public RequestDto getRequestById(Long requestId) {
        return itemRequestRepository.findById(requestId)
                .map(itemRequest -> {
                    List<ItemAnswerDto> answers = itemRepository.findAllByRequestId(requestId);
                    return RequestMapper.mapToDto(itemRequest, answers);
                })
                .orElseThrow(() -> new NotFoundException("Request not found"));
    }
}
