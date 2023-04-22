package ru.practicum.shareit.item.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Item {
    Long id;
    @NotBlank
    String name;
    @NotBlank
    String description;
    Boolean available;
    Long owener;
    Long request;
}