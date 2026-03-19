package com.bankcards.service;

import com.bankcards.dto.card.CardDTO;
import com.bankcards.dto.card.CreateCardDTO;

import java.util.List;

public interface CardService {
    List<CardDTO> getCardsByUserId(long userId);
    CardDTO addCard(long userId, CreateCardDTO dto);
    void deleteCard(long userId, int cardId);
    CardDTO reissueCard(long userId, int cardId);
    CardDTO getCard(long userId, int cardId);
    /** Lookup any card by number (e.g. for transfer target). Returns null if not found. */
    CardDTO findCardByNumber(String number);
}
