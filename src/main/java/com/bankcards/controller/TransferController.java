package com.bankcards.controller;

import com.bankcards.dto.transfer.TopUpRequestDTO;
import com.bankcards.dto.transfer.TransferDTO;
import com.bankcards.dto.transfer.TransferRequestDTO;
import com.bankcards.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

//Используется в account.html
@RestController
@RequestMapping("/users/{userId}")
public class TransferController {
    @Autowired
    private TransferService transferService;

    private void ensureUserAccess(long userId, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        long authenticatedUserId = Long.parseLong(authentication.getName());
        if (authenticatedUserId != userId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
    }

    //Пополняет выбранную карту с помощью существующей карты-источника в базе данных.
    @PostMapping("/cards/{cardId}/top-up")
    public void topUp(@PathVariable long userId, @PathVariable int cardId, @Valid @RequestBody TopUpRequestDTO dto, Authentication authentication) {
        ensureUserAccess(userId, authentication);
        transferService.topUp(userId, cardId, dto.getAmount(), dto.getSourceCardNumber());
    }

    //Осуществляет перевод средств с карты на карту.
    @PostMapping("/transfers")
    public TransferDTO transfer(@PathVariable long userId, @Valid @RequestBody TransferRequestDTO dto, Authentication authentication) {
        ensureUserAccess(userId, authentication);
        return transferService.transfer(userId, dto);
    }
}
