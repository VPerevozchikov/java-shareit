package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Component
public class ItemDto {
    Long id;
    @NotNull
    @NotBlank
    String name;
    @Size(min = 1, max = 200)
    String description;
    boolean available;
    Long owener;
    Long request;

}
