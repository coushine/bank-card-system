package com.bankcards.dto.card;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateCardDTO {
    @NotBlank(message = "Card number is required")
    private String number;

    @NotNull(message = "Expiry date is required")
    private LocalDate expiryDate;
}
