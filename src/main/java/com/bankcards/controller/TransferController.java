package com.bankcards.controller;

import com.bankcards.dto.transfer.TopUpRequestDTO;
import com.bankcards.dto.transfer.TransferDTO;
import com.bankcards.dto.transfer.TransferRequestDTO;
import com.bankcards.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{userId}")
public class TransferController {

    @Autowired
    private TransferService transferService;

    @PostMapping("/cards/{cardId}/top-up")
    public void topUp(@PathVariable long userId, @PathVariable int cardId, @Valid @RequestBody TopUpRequestDTO dto) {
        transferService.topUp(userId, cardId, dto.getAmount());
    }

    @PostMapping("/transfers")
    public TransferDTO transfer(@PathVariable long userId, @Valid @RequestBody TransferRequestDTO dto) {
        return transferService.transfer(userId, dto);
    }
}
