package com.bankcards.dto.transfer;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequestDTO {
    @NotNull(message = "Введите номер карты-источника")
    private Long fromCardId;

    @NotNull(message = "Введите номер карты-получателя")
    private Long toCardId;

    @NotNull(message = "Введите сумму")
    @DecimalMin(value = "0.01", message = "Сумма должна быть положительной")
    private BigDecimal amount;
}
