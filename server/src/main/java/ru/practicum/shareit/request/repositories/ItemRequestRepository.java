package ru.practicum.shareit.request.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query(value = "select * from requests as rq " +
            "where rq.requestor_id = ? " +
            "order by create_date DESC", nativeQuery = true)
    List<Optional<ItemRequest>> findItemRequestsByUserId(Long userId);


    @Query(value = "select * from requests as rq " +
            "where not rq.requestor_id = ?1 " +
            "AND rq.id >= ?2 " +
            "order by create_date DESC " +
            "limit ?3", nativeQuery = true)
    List<Optional<ItemRequest>> findItemRequestsByAnotherUsers(Long userId, Integer from, Integer size);
}
