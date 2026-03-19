package com.bankcards.dto.card;

import com.bankcards.entity.enums.CardStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Accessors(chain = true)
//@Schema(description = "DTO для представления информации о банковской карте")
public class CardDTO {

    //@NotNull
    //@Schema(description = "ID карты", example = "101", required = true)
    private Long id;

    //@NotNull
    //@Schema(description = "Маскированный номер карты", example = "**** **** **** 1234", required = true)
    private String maskedNumber;

//    @NotNull
//    @PositiveOrZero
    //@Schema(description = "Баланс карты", example = "1000.00", required = true)
    private BigDecimal balance;

    //@NotNull
    //@Schema(description = "Срок действия карты", example = "2025-12-31", required = true)
    private LocalDate expiryDate;

    //@NotNull
    //@Schema(description = "Статус карты", example = "ACTIVE", required = true)
    private CardStatus status;
}