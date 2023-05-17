package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    @Min(1)
    private Long id;
    @NotBlank
    private String name;
    @Email
    private String email;
}

