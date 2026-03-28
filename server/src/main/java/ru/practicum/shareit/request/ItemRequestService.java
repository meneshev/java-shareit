package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.CreateItemRequestReqDto;
import ru.practicum.shareit.request.dto.RequestDto;
import java.util.List;

public interface ItemRequestService {
    RequestDto saveRequest(CreateItemRequestReqDto createRequest, Long userId);

    List<RequestDto> getRequestsByUser(Long userId);

    List<RequestDto> getRequestsByOtherUsers(Long userId);

    RequestDto getRequestById(Long requestId);
}
