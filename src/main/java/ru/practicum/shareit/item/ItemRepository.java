package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import java.util.List;

public interface ItemRepository  extends JpaRepository<Item, Long> {
    List<Item> findByOwner(User user);

    @Query("""
    select it
    from Item it
    where (it.name ilike concat('%', ?1, '%') or it.description ilike concat('%', ?2, '%'))
    and it.isAvailable = ?3
    """)
    List<Item> findBySearchString(String name, String description, Boolean isAvailable);

    @Query("""
    select
        it.id id,
        it.name name
    from Item it
    where it.id = ?1
    """)
    ItemShort findShort(Long itemId);

    List<Item> findAllByOwner_Id(Long ownerId);
}