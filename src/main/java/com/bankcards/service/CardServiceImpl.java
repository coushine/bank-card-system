package com.bankcards.service;

import com.bankcards.dto.card.CardDTO;
import com.bankcards.dto.card.CreateCardDTO;
import com.bankcards.entity.CardEntity;
import com.bankcards.entity.UserEntity;
import com.bankcards.entity.enums.BlockRequestStatus;
import com.bankcards.entity.enums.CardStatus;
import com.bankcards.exception.DuplicateResourceException;
import com.bankcards.exception.IncorrectDataException;
import com.bankcards.repository.CardRepository;
import com.bankcards.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardServiceImpl implements CardService {

    private static final Logger log = LoggerFactory.getLogger(CardServiceImpl.class);

    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private UserRepository userRepository;

    private static String maskNumber(String number) {
        if (number == null || number.length() < 4) return "****";
        String digits = number.replaceAll("\\D", "");
        if (digits.length() < 4) return "****";
        return "**** **** **** " + digits.substring(digits.length() - 4);
    }

    private static String normalizeCardNumber(String rawNumber) {
        if (rawNumber == null) {
            throw new IncorrectDataException("Card number is required");
        }
        String digits = rawNumber.replaceAll("\\D", "");
        if (digits.length() != 16) {
            throw new IncorrectDataException("Card number must contain 16 digits");
        }
        return digits.replaceAll("(\\d{4})(?=\\d)", "$1 ").trim();
    }

    private static CardDTO toDto(CardEntity e) {
        CardDTO dto = new CardDTO();
        dto.setId((long) e.getId());
        dto.setMaskedNumber(maskNumber(e.getNumber()));
        dto.setFullNumber(e.getNumber());
        dto.setBalance(e.getBalance());
        dto.setExpiryDate(e.getExpiryDate());
        dto.setStatus(e.getStatus());
        return dto;
    }

    @Override
    public List<CardDTO> getCardsByUserId(long userId) {
        List<CardEntity> list = cardRepository.findByUser_Id((int) userId);
        return list.stream().map(CardServiceImpl::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CardDTO addCard(long userId, CreateCardDTO dto) {
        UserEntity user = userRepository.findById((long) userId).orElseThrow(() ->
                new IncorrectDataException("User not found"));
        String number = normalizeCardNumber(dto.getNumber());
        if (cardRepository.findByNumber(number).isPresent()) {
            throw new DuplicateResourceException("Card with this number already exists");
        }
        CardEntity card = new CardEntity();
        card.setNumber(number);
        card.setUser(user);
        card.setExpiryDate(dto.getExpiryDate());
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.ZERO);
        card.setBlockRequestStatus(BlockRequestStatus.NONE);
        card = cardRepository.save(card);
        return toDto(card);
    }

    @Override
    @Transactional
    public void deleteCard(long userId, int cardId) {
        CardEntity card = cardRepository.findById(cardId).orElseThrow(()
                -> new IncorrectDataException("Card not found"));
        if (card.getUser().getId() != userId) {
            throw new IncorrectDataException("Card does not belong to user");
        }
        cardRepository.delete(card);
    }

    @Override
    @Transactional
    public CardDTO reissueCard(long userId, int cardId) {
        CardEntity card = cardRepository.findById(cardId).orElseThrow(()
                -> new IncorrectDataException("Card not found"));
        if (card.getUser().getId() != userId) {
            throw new IncorrectDataException("Card does not belong to user");
        }
        card.setExpiryDate(LocalDate.now().plusYears(3));
        card = cardRepository.save(card);
        return toDto(card);
    }

    @Override
    public CardDTO getCard(long userId, int cardId) {
        CardEntity card = cardRepository.findById(cardId).orElseThrow(() -> new IncorrectDataException("Card not found"));
        if (card.getUser().getId() != userId) {
            throw new IncorrectDataException("Card does not belong to user");
        }
        return toDto(card);
    }

    @Override
    public CardDTO findCardByNumber(String number) {
        if (number == null) return null;

        String inputDigitsOnly = number.replaceAll("\\D", "");
        if (inputDigitsOnly.isEmpty()) return null;

        log.info("lookup card number raw='{}' digitsOnly='{}'", number, inputDigitsOnly);

        String formattedInput = inputDigitsOnly.replaceAll("(\\d{4})(?=\\d)", "$1 ").trim();
        CardDTO exact = cardRepository.findByNumber(formattedInput).map(CardServiceImpl::toDto).orElse(null);
        if (exact != null) return exact;

        CardDTO fallback = cardRepository.findAll().stream()
                .filter(c -> c.getNumber() != null && c.getNumber().replaceAll("\\D", "")
                        .equals(inputDigitsOnly))
                .findFirst()
                .map(CardServiceImpl::toDto)
                .orElse(null);

        if (fallback != null) {
            log.info("lookup card found by fallback. id={}", fallback.getId());
        } else {
            log.info("lookup card not found for digitsOnly='{}'", inputDigitsOnly);
        }
        return fallback;
    }
}
