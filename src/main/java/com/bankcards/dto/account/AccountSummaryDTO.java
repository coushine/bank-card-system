package com.bankcards.dto.account;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AccountSummaryDTO {

    //Баланс всех карт
    private BigDecimal totalBalance;
    private int cardsCount;
    private List<CardSummaryItem> cards;
    private List<RecentTransactionDTO> recentTransactions;

    @Data
    public static class CardSummaryItem {
        private Long id;
        private String maskedNumber;
    }

    @Data
    public static class RecentTransactionDTO {
        private Long fromCardId;
        private String fromCardMaskedNumber;
        private Long toCardId;
        private String toCardMaskedNumber;
        private BigDecimal amount;
        private String description;
        private LocalDateTime transferTime;
    }
}
