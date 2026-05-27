package com.bankcards.dto.card;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateCardDTO {
    @NotBlank(message = "Необходимо написать номер карты")
    private String number;

    @NotNull(message = "Необходимо выбрать срок действия карты")
    private LocalDate expiryDate;
}
