package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments", schema = "public")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column
    String text;
    @Column(name = "item_id")
    Long itemId;
    @ManyToOne
    @JoinColumn(name = "author_id")
    User author;
    @Column
    LocalDateTime created;

    public Comment() {
    }
}
