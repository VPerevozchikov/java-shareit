package ru.practicum.shareit.request.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "requests", schema = "public")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column
    String description;
    @Column(name = "requestor_id")
    Long requestor;
    @Column(name = "create_date")
    LocalDateTime created;
}
