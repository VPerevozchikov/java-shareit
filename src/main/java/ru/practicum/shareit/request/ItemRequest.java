package ru.practicum.shareit.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ItemRequest {
    Long id;
    @Size(min = 1, max = 200)
    String description;
    boolean available;
    Long request;
    LocalDateTime created;
}
