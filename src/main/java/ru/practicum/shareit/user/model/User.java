package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.persistence.*;


@Data
@Entity
@Table(name = "users", schema = "public")


public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column
    String name;
    @Column
    String email;

    public User() {
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}