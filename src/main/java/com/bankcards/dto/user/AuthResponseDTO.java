package com.bankcards.dto.user;

import lombok.Data;

@Data
public class AuthResponseDTO {
    private String token;
    private UserDTO user;
}
