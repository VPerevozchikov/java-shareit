package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Data
@Entity
@Table(name = "items", schema = "public")

public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column
    String name;
    @Column
    String description;
    @Column(name = "is_available")
    Boolean available;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    User user;
    @Column(name = "request_id")
    Long request;

    public Item() {
    }

    public Item(String name, String description, Boolean available, User user) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.user = user;
    }
}