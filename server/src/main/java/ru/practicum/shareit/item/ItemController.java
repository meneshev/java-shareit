package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto save(@RequestBody CreateItemRequest request,
                        @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemService.createItem(request, userId);
    }

    @PostMapping("/{item-id}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto saveComment(@RequestBody CreateCommentRequest request,
                                  @PathVariable("item-id") Long itemId,
                                  @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemService.createComment(request, itemId, userId);
    }

    @PatchMapping("/{item-id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(@RequestBody UpdateItemRequest request,
                          @RequestHeader(name = "X-Sharer-User-Id") Long userId,
                          @PathVariable("item-id")  Long itemId) {
        return itemService.updateItem(request, userId, itemId);
    }

    @DeleteMapping("/{item-id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable("item-id")  Long itemId,
                                                      @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        itemService.deleteItem(itemId, userId);
        Map<String, String> response = Map.of("message", "Item has been deleted");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{item-id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getItemById(@PathVariable("item-id")  Long itemId,
                               @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemService.getItemDtoById(itemId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getItemsByUserId(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> search(@RequestParam("text") String searchString,
                                @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemService.findItems(searchString, userId);
    }
}
