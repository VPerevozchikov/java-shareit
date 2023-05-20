package ru.practicum.shareit.booking.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking findByStartEquals(LocalDateTime start);

    @Query(value = "select * from bookings as bk " +
            "where bk.booker_id = ? " +
            "order by start_date DESC", nativeQuery = true)
    List<Optional<Booking>> findBookersByBookerId(Long bookerId);

    @Query(value = "select * from bookings as bk " +
            "where bk.booker_id = ?1 " +
            "AND upper (status) LIKE upper (?2) " +
            "order by start_date DESC", nativeQuery = true)
    List<Optional<Booking>> findBookersByBookerIdAndTypicalStatus(Long bookerId, String status);

    @Query(value = "select * from bookings as bk " +
            "where bk.booker_id = ?1 " +
            "AND bk.start_date >= cast(?2 as timestamp) " +
            "order by start_date DESC", nativeQuery = true)
    List<Optional<Booking>> findFutureBookersByBookerId(Long bookerId, LocalDateTime currentDateTime);

    @Query(value = "select * from bookings as bk " +
            "where bk.booker_id = ?1 " +
            "AND bk.end_date < cast(?2 as timestamp) " +
            "order by start_date DESC", nativeQuery = true)
    List<Optional<Booking>> findPastBookersByBookerId(Long bookerId, LocalDateTime currentDateTime);

    @Query(value = "select * from bookings as bk " +
            "where bk.booker_id = ?1 " +
            "AND bk.start_date < cast(?2 as timestamp) " +
            "AND bk.end_date > cast(?2 as timestamp) " +
            "order by start_date DESC", nativeQuery = true)
    List<Optional<Booking>> findCurrentBookersByBookerId(Long bookerId, LocalDateTime currentDateTime);

    @Query(value = "select * from bookings as bk " +
            "join items as i on bk.item_id = i.id " +
            "where i.owner_id = ? " +
            "order by bk.start_date DESC", nativeQuery = true)
    List<Optional<Booking>> findBookingsByOwnerId(Long ownerId);

    @Query(value = "select * from bookings as bk " +
            "join items as i on bk.item_id = i.id " +
            "where i.owner_id = ?1 " +
            "AND upper (bk.status) LIKE upper (?2) " +
            "order by bk.start_date DESC", nativeQuery = true)
    List<Optional<Booking>> findBookersByOwnerIdAndTypicalStatus(Long ownerId, String status);

    @Query(value = "select * from bookings as bk " +
            "join items as i on bk.item_id = i.id " +
            "where i.owner_id = ?1 " +
            "AND bk.start_date >= cast(?2 as timestamp) " +
            "order by bk.start_date DESC", nativeQuery = true)
    List<Optional<Booking>> findFutureBookersByOwnerId(Long ownerId, LocalDateTime currentDateTime);

    @Query(value = "select * from bookings as bk " +
            "join items as i on bk.item_id = i.id " +
            "where i.owner_id = ?1 " +
            "AND bk.end_date < cast(?2 as timestamp) " +
            "order by bk.start_date DESC", nativeQuery = true)
    List<Optional<Booking>> findPastBookersByOwnerId(Long ownerId, LocalDateTime currentDateTime);

    @Query(value = "select * from bookings as bk " +
            "join items as i on bk.item_id = i.id " +
            "where i.owner_id = ?1 " +
            "AND bk.start_date < cast(?2 as timestamp) " +
            "AND bk.end_date > cast(?2 as timestamp) " +
            "order by bk.start_date DESC", nativeQuery = true)
    List<Optional<Booking>> findCurrentBookersByOwnerId(Long ownerId, LocalDateTime currentDateTime);

    List<Optional<Booking>> findBookingByItemIdOrderByStartDesc(Long itemId);

}