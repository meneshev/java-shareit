package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("""
    select new ru.practicum.shareit.booking.dto.BookingDto(
        b.id id,
        new ru.practicum.shareit.user.UserShort(b.booker.id) booker,
        new ru.practicum.shareit.item.ItemShort(b.item.id, b.item.name) item,
        b.start start,
        b.end end,
        b.status status)
    from Booking b
    join b.booker
    join b.item
    where b.booker.id = ?1
    order by b.start desc
    """)
    List<BookingDto> findByBookerId(Long userId);

    @Query("""
    select new ru.practicum.shareit.booking.dto.BookingDto(
        b.id id,
        new ru.practicum.shareit.user.UserShort(b.booker.id) booker,
        new ru.practicum.shareit.item.ItemShort(b.item.id, b.item.name) item,
        b.start start,
        b.end end,
        b.status status)
    from Booking b
    join b.booker
    join b.item
    where b.booker.id = ?1 and b.status = ?2
    order by b.start desc
    """)
    List<BookingDto> findByBookerIdAndStatusId(Long userId, BookingStatus status);

    @Query("""
    select new ru.practicum.shareit.booking.dto.BookingDto(
        b.id id,
        new ru.practicum.shareit.user.UserShort(b.booker.id) booker,
        new ru.practicum.shareit.item.ItemShort(b.item.id, b.item.name) item,
        b.start start,
        b.end end,
        b.status status)
    from Booking b
    join b.booker
    join b.item
    where b.item.owner.id = ?1
    order by b.start desc
    """)
    List<BookingDto> findByOwnerId(Long userId);

    @Query("""
    select new ru.practicum.shareit.booking.dto.BookingDto(
        b.id id,
        new ru.practicum.shareit.user.UserShort(b.booker.id) booker,
        new ru.practicum.shareit.item.ItemShort(b.item.id, b.item.name) item,
        b.start start,
        b.end end,
        b.status status)
    from Booking b
    join b.booker
    join b.item
    where b.item.owner.id = ?1 and b.status = ?2
    order by b.start desc
    """)
    List<BookingDto> findByOwnerIdAndStatusId(Long userId, BookingStatus status);

    @Query("""
    select new ru.practicum.shareit.booking.dto.BookingDto(
        b.id id,
        new ru.practicum.shareit.user.UserShort(b.booker.id) booker,
        new ru.practicum.shareit.item.ItemShort(b.item.id, b.item.name) item,
        b.start start,
        b.end end,
        b.status status)
    from Booking b
    join b.booker
    join b.item
    where b.booker.id = ?1
        and (CURRENT TIMESTAMP between b.start and b.end)
    order by b.id asc
    """)
    List<BookingDto> getCurrentBookingsByBookerId(Long userId);

    @Query("""
    select new ru.practicum.shareit.booking.dto.BookingDto(
        b.id id,
        new ru.practicum.shareit.user.UserShort(b.booker.id) booker,
        new ru.practicum.shareit.item.ItemShort(b.item.id, b.item.name) item,
        b.start start,
        b.end end,
        b.status status)
    from Booking b
    join b.booker
    join b.item
    where b.booker.id = ?1
        and b.end < CURRENT_TIMESTAMP
    order by b.id asc
    """)
    List<BookingDto> getPastBookingsByBookerId(Long userId);

    @Query("""
    select new ru.practicum.shareit.booking.dto.BookingDto(
        b.id id,
        new ru.practicum.shareit.user.UserShort(b.booker.id) booker,
        new ru.practicum.shareit.item.ItemShort(b.item.id, b.item.name) item,
        b.start start,
        b.end end,
        b.status status)
    from Booking b
    join b.booker
    join b.item
    where b.booker.id = ?1
        and b.start > CURRENT_TIMESTAMP
    order by b.id asc
    """)
    List<BookingDto> getFutureBookingsByBookerId(Long userId);

    @Query("""
    select new ru.practicum.shareit.booking.dto.BookingDto(
        b.id id,
        new ru.practicum.shareit.user.UserShort(b.booker.id) booker,
        new ru.practicum.shareit.item.ItemShort(b.item.id, b.item.name) item,
        b.start start,
        b.end end,
        b.status status)
    from Booking b
    join b.booker
    join b.item
    where b.item.owner.id = ?1
        and (CURRENT_TIMESTAMP between b.start and b.end)
    order by b.id asc
    """)
    List<BookingDto> getCurrentBookingsByOwnerId(Long userId);

    @Query("""
    select new ru.practicum.shareit.booking.dto.BookingDto(
        b.id id,
        new ru.practicum.shareit.user.UserShort(b.booker.id) booker,
        new ru.practicum.shareit.item.ItemShort(b.item.id, b.item.name) item,
        b.start start,
        b.end end,
        b.status status)
    from Booking b
    join b.booker
    join b.item
    where b.item.owner.id = ?1
        and b.end < CURRENT_TIMESTAMP
    order by b.id asc
    """)
    List<BookingDto> getPastBookingsByOwnerId(Long userId);

    @Query("""
    select new ru.practicum.shareit.booking.dto.BookingDto(
        b.id id,
        new ru.practicum.shareit.user.UserShort(b.booker.id) booker,
        new ru.practicum.shareit.item.ItemShort(b.item.id, b.item.name) item,
        b.start start,
        b.end end,
        b.status status)
    from Booking b
    join b.booker
    join b.item
    where b.item.owner.id = ?1
        and b.start > CURRENT_TIMESTAMP
    order by b.id asc
    """)
    List<BookingDto> getFutureBookingsByOwnerId(Long userId);

    @Query("""
    select b.end
    from Booking b
    where b.item.id = ?1 and b.end < CURRENT_TIMESTAMP
    order by b.id desc
    limit 1
    """)
    LocalDateTime getLastEndDateByItemId(Long itemId);

    @Query("""
    select b.start
    from Booking b
    where b.item.id = ?1 and b.start > CURRENT TIMESTAMP
    order by b.id asc
    limit 1
    """)
    LocalDateTime getNextStartDateByItemId(Long itemId);
}
