package com.bankcards.service;

import com.bankcards.dto.transfer.TransferDTO;
import com.bankcards.dto.transfer.TransferRequestDTO;
import com.bankcards.entity.CardEntity;
import com.bankcards.entity.TransferEntity;
import com.bankcards.exception.IncorrectDataException;
import com.bankcards.repository.CardRepository;
import com.bankcards.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransferServiceImpl implements TransferService {

    @Autowired
    private TransferRepository transferRepository;
    @Autowired
    private CardRepository cardRepository;

    private static String maskNumber(String number) {
        if (number == null || number.length() < 4) return "****";
        String digits = number.replaceAll("\\D", "");
        if (digits.length() < 4) return "****";
        return "**** **** **** " + digits.substring(digits.length() - 4);
    }

    private static TransferDTO toDto(TransferEntity e) {
        TransferDTO dto = new TransferDTO();
        dto.setId((long) e.getId());
        dto.setTransferTime(e.getTransferTime());
        dto.setFromCardId((long) e.getFromCard().getId());
        dto.setFromCardMaskedNumber(maskNumber(e.getFromCard().getNumber()));
        dto.setToCardId((long) e.getToCard().getId());
        dto.setToCardMaskedNumber(maskNumber(e.getToCard().getNumber()));
        dto.setAmount(e.getAmount());
        return dto;
    }

    @Override
    @Transactional
    public TransferDTO transfer(long userId, TransferRequestDTO dto) {
        int fromId = dto.getFromCardId().intValue();
        int toId = dto.getToCardId().intValue();
        if (fromId == toId) {
            throw new IncorrectDataException("Карты должны различаться");
        }
        CardEntity fromCard = cardRepository.findById(fromId).orElseThrow(() -> new IncorrectDataException("Карты не найдена"));
        CardEntity toCard = cardRepository.findById(toId).orElseThrow(() -> new IncorrectDataException("Карта для перевода не найдена"));
        if (fromCard.getUser().getId() != userId) {
            throw new IncorrectDataException("Source card does not belong to you");
        }
        if (fromCard.getBalance().compareTo(dto.getAmount()) < 0) {
            throw new IncorrectDataException("Insufficient balance");
        }
        fromCard.setBalance(fromCard.getBalance().subtract(dto.getAmount()));
        toCard.setBalance(toCard.getBalance().add(dto.getAmount()));
        cardRepository.save(fromCard);
        cardRepository.save(toCard);
        TransferEntity tx = new TransferEntity();
        tx.setTransferTime(LocalDateTime.now());
        tx.setFromCard(fromCard);
        tx.setToCard(toCard);
        tx.setAmount(dto.getAmount());
        tx = transferRepository.save(tx);
        return toDto(tx);
    }

    @Override
    @Transactional
    public void topUp(long userId, int cardId, BigDecimal amount, String sourceCardNumber) { //Пополнение
        CardEntity toCard = cardRepository.findById(cardId)
                .orElseThrow(() -> new IncorrectDataException("Карта не найдена"));
        if (toCard.getUser().getId() != userId) {
            throw new IncorrectDataException("Введите корректный номер карты");
        }

        String sourceDigits = sourceCardNumber == null ? "" : sourceCardNumber.replaceAll("\\D", "");
        CardEntity fromCard = cardRepository.findAll().stream()
                .filter(c -> c.getNumber() != null && c.getNumber().replaceAll("\\D", "").equals(sourceDigits))
                .findFirst()
                .orElseThrow(() -> new IncorrectDataException("Карта не найдена"));

        if (fromCard.getId() == toCard.getId()) {
            throw new IncorrectDataException("Карты должны отличаться");
        }
        if (fromCard.getBalance().compareTo(amount) < 0) {
            throw new IncorrectDataException("Недостаточно средств на карте");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(amount));
        toCard.setBalance(toCard.getBalance().add(amount));
        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        TransferEntity tx = new TransferEntity();
        tx.setTransferTime(LocalDateTime.now());
        tx.setFromCard(fromCard);
        tx.setToCard(toCard);
        tx.setAmount(amount);
        transferRepository.save(tx);
    }

    @Override
    public List<TransferDTO> getRecentTransfersByUserId(long userId, int limit) {
        List<CardEntity> userCards = cardRepository.findByUser_Id((int) userId);
        if (userCards.isEmpty()) return new ArrayList<>();
        List<Integer> cardIds = userCards.stream().map(CardEntity::getId).collect(Collectors.toList());
        List<TransferEntity> list = transferRepository.findByFromCard_IdInOrToCard_IdInOrderByTransferTimeDesc(cardIds, cardIds, PageRequest.of(0, limit));
        return list.stream().map(TransferServiceImpl::toDto).collect(Collectors.toList());
    }
}
