package com.bankcards.controller;

import com.bankcards.dto.card.CardDTO;
import com.bankcards.dto.card.CreateCardDTO;
import com.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/cards")
public class CardController {

    @Autowired
    private CardService cardService;

    private void ensureUserAccess(long userId, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        long authenticatedUserId = Long.parseLong(authentication.getName());
        if (authenticatedUserId != userId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
    }

    @Schema(description = "Получаем карты пользователя по id")
    @GetMapping
    public List<CardDTO> getCards(@PathVariable long userId, Authentication authentication) {
        ensureUserAccess(userId, authentication);
        return cardService.getCardsByUserId(userId);
    }

    @PostMapping
    public CardDTO addCard(@PathVariable long userId, @Valid @RequestBody CreateCardDTO dto, Authentication authentication) {
        ensureUserAccess(userId, authentication);
        return cardService.addCard(userId, dto);
    }

    @DeleteMapping("/{cardId}")
    public void deleteCard(@PathVariable long userId, @PathVariable int cardId, Authentication authentication) {
        ensureUserAccess(userId, authentication);
        cardService.deleteCard(userId, cardId);
    }

    //Перевыпуск карты
    @PutMapping("/{cardId}/reissue")
    public CardDTO reissueCard(@PathVariable long userId, @PathVariable int cardId, Authentication authentication) {
        ensureUserAccess(userId, authentication);
        return cardService.reissueCard(userId, cardId);
    }

    @GetMapping("/{cardId}")
    public CardDTO getCard(@PathVariable long userId, @PathVariable int cardId, Authentication authentication) {
        ensureUserAccess(userId, authentication);
        return cardService.getCard(userId, cardId);
    }

    //Найти любую карту по номеру для перевода
    @GetMapping("/lookup")
    public ResponseEntity<CardDTO> lookupCard(@PathVariable long userId, @RequestParam String number, Authentication authentication) {
        ensureUserAccess(userId, authentication);
        CardDTO card = cardService.findCardByNumber(number);
        if (card == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(card);
    }
}
