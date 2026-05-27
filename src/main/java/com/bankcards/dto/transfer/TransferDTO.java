package com.bankcards.dto.transfer;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransferDTO {
    private Long id;

    @NotBlank
    private LocalDateTime transferTime;

    @NotBlank
    private Long fromCardId;

    @NotBlank
    private String fromCardMaskedNumber;

    @NotBlank
    private Long toCardId;

    @NotBlank
    private String toCardMaskedNumber;

    @NotBlank
    private BigDecimal amount;
}
