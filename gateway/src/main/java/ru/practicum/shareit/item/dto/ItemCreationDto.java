package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ItemCreationDto {
    @NotNull
    @NotBlank
    String name;
    String description;
    Boolean available;
    //    User user;
    Long requestId;
}
