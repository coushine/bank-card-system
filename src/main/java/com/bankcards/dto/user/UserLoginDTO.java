package com.bankcards.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginDTO {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
