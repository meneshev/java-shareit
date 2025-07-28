package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto save(@Valid @RequestBody CreateItemRequest request,
                        @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemService.createItem(request, userId);
    }

    @PatchMapping("/{item-id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(@RequestBody UpdateItemRequest request,
                          @RequestHeader(name = "X-Sharer-User-Id") Long userId,
                          @PathVariable("item-id")  Long itemId) {
        return itemService.updateItem(request, userId, itemId);
    }

    @DeleteMapping("/{item-id}")
    public ResponseEntity<String> delete(@PathVariable("item-id")  Long itemId,
                                         @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        if (itemService.deleteItem(itemId, userId)) {
            return new ResponseEntity<>("Item deleted", HttpStatus.OK);
        } else  {
            return new ResponseEntity<>("Error during deleting", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{item-id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getItemById(@PathVariable("item-id")  Long itemId) {
        return itemService.getItemById(itemId);
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
