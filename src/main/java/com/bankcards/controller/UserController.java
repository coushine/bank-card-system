package com.bankcards.controller;

import com.bankcards.dto.account.AccountSummaryDTO;
import com.bankcards.dto.user.UserDTO;
import com.bankcards.dto.user.UserLoginDTO;
import com.bankcards.dto.transfer.TransferDTO;
import com.bankcards.entity.UserEntity;
import com.bankcards.service.CardService;
import com.bankcards.service.TransferService;
import com.bankcards.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private CardService cardService;
    @Autowired
    private TransferService transferService;

    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable Long id){
        return userService.getUserDTO(id);
    }

    @PostMapping("/registration")
    public UserDTO registerUser(@Valid @RequestBody UserEntity userEntity){
        return userService.registerUser(userEntity);
    }

    @PostMapping("/authorization")
    public ResponseEntity<?> authorizeUser(@Valid @RequestBody UserLoginDTO login) {
        if (!userService.checkUser(login)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDTO user = userService.getUserDTOByEmail(login.getEmail());
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/{id}/summary")
    public AccountSummaryDTO getAccountSummary(@PathVariable long id) {
        var cards = cardService.getCardsByUserId(id);
        BigDecimal totalBalance = cards.stream().map(c -> c.getBalance()).reduce(BigDecimal.ZERO, BigDecimal::add);
        List<TransferDTO> recent = transferService.getRecentTransfersByUserId(id, 10);
        AccountSummaryDTO dto = new AccountSummaryDTO();
        dto.setTotalBalance(totalBalance);
        dto.setCardsCount(cards.size());
        dto.setCards(cards.stream().map(c -> {
            var item = new AccountSummaryDTO.CardSummaryItem();
            item.setId(c.getId());
            item.setMaskedNumber(c.getMaskedNumber());
            return item;
        }).collect(Collectors.toList()));
        var userCardIds = cards.stream().map(c -> c.getId()).collect(Collectors.toSet());
        dto.setRecentTransactions(recent.stream().map(t -> {
            var r = new AccountSummaryDTO.RecentTransactionDTO();
            r.setFromCardId(t.getFromCardId());
            r.setFromCardMaskedNumber(t.getFromCardMaskedNumber());
            r.setToCardId(t.getToCardId());
            r.setToCardMaskedNumber(t.getToCardMaskedNumber());
            boolean userIsSender = userCardIds.contains(t.getFromCardId());
            r.setAmount(userIsSender ? t.getAmount().negate() : t.getAmount());
            r.setDescription(userIsSender ? ("To " + t.getToCardMaskedNumber()) : ("From " + t.getFromCardMaskedNumber()));
            return r;
        }).collect(Collectors.toList()));
        return dto;
    }
}
