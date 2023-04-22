package ru.practicum.shareit.user.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */

@Getter
@Setter
@ToString
@NoArgsConstructor

public class User {
    Long id;

    @NotNull
    @NotBlank
    String name;

    @NotNull
    @NotBlank
    @Email
    String email;
}