package com.bankcards.entity;

//import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
//import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transfers")

@Getter
@Setter
@RequiredArgsConstructor
//@Hidden
public class TransferEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "transfer_time", nullable = false)
    private LocalDateTime transferTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_card_id", nullable = false)
    private CardEntity fromCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_card_id", nullable = false)
    private CardEntity toCard;

    @Column(name = "amount", nullable = false)
    //@Positive
    private BigDecimal amount;
}
