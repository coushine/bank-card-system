package com.bankcards.dto.transfer;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransferDTO {
    private Long id;
    private LocalDateTime transferTime;
    private Long fromCardId;
    private String fromCardMaskedNumber;
    private Long toCardId;
    private String toCardMaskedNumber;
    private BigDecimal amount;
}
