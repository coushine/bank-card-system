package com.bankcards.entity;

import com.bankcards.entity.enums.BlockRequestStatus;
import com.bankcards.entity.enums.CardStatus;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cards")
@Getter
@Setter
@RequiredArgsConstructor
@Accessors(chain = true)
@Hidden
public class CardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "number", nullable = false)
    private String number;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CardStatus status;

    @Column(name = "balance", nullable = false)
    @PositiveOrZero
    private BigDecimal balance;

    @Column(name = "block_request_status")
    private BlockRequestStatus blockRequestStatus;
}
