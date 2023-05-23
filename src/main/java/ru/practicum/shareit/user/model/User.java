package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@Builder
public class User {
    @Min(1)
    private Long id;
    @NotBlank
    private String name;
    @Email
    @NotNull
    private String email;
}
