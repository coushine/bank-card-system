package com.bankcards.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserLoginDTO {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
