package com.bankcards.repository;

import com.bankcards.entity.TransferEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferRepository extends JpaRepository<TransferEntity, Integer> {
    List<TransferEntity> findByFromCard_IdInOrToCard_IdInOrderByTransferTimeDesc(List<Integer> fromCardIds, List<Integer> toCardIds, Pageable pageable);
}
