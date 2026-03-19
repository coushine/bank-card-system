package com.bankcards.service;

import com.bankcards.dto.transfer.TransferDTO;
import com.bankcards.dto.transfer.TransferRequestDTO;

import java.math.BigDecimal;
import java.util.List;

public interface TransferService {
    TransferDTO transfer(long userId, TransferRequestDTO dto);
    void topUp(long userId, int cardId, BigDecimal amount);
    List<TransferDTO> getRecentTransfersByUserId(long userId, int limit);
}
