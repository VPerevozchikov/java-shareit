package ru.practicum.shareit.item.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Item findByNameContainingIgnoreCase(String name);

    List<Item> findItemsByUser(User user);

    @Query(value = "select * from items as it " +
            "where upper(it.name) like upper(concat('%', ?1, '%'))" +
            " or upper (it.description) like upper (concat('%', ?1, '%'))", nativeQuery = true)
    List<Optional<Item>> searchItemsByText(String text);
}
