package com.bankcards.controller;

import com.bankcards.dto.card.CardDTO;
import com.bankcards.dto.card.CreateCardDTO;
import com.bankcards.service.CardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/cards")
public class CardController {

    @Autowired
    private CardService cardService;

    @GetMapping
    public List<CardDTO> getCards(@PathVariable long userId) {
        return cardService.getCardsByUserId(userId);
    }

    @PostMapping
    public CardDTO addCard(@PathVariable long userId, @Valid @RequestBody CreateCardDTO dto) {
        return cardService.addCard(userId, dto);
    }

    @DeleteMapping("/{cardId}")
    public void deleteCard(@PathVariable long userId, @PathVariable int cardId) {
        cardService.deleteCard(userId, cardId);
    }

    //Перевыпуск карты
    @PutMapping("/{cardId}/reissue")
    public CardDTO reissueCard(@PathVariable long userId, @PathVariable int cardId) {
        return cardService.reissueCard(userId, cardId);
    }

    @GetMapping("/{cardId}")
    public CardDTO getCard(@PathVariable long userId, @PathVariable int cardId) {
        return cardService.getCard(userId, cardId);
    }

    /** Lookup any card by number (for transfer target). */
    @GetMapping("/lookup")
    public CardDTO lookupCard(@RequestParam String number) {
        return cardService.findCardByNumber(number);
    }
}
