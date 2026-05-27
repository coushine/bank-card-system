package com.bankcards.controller;

import com.bankcards.dto.account.AccountSummaryDTO;
import com.bankcards.dto.user.AuthResponseDTO;
import com.bankcards.dto.user.UserDTO;
import com.bankcards.dto.user.UserLoginDTO;
import com.bankcards.dto.user.UpdateUserProfileDTO;
import com.bankcards.dto.transfer.TransferDTO;
import com.bankcards.entity.UserEntity;
import com.bankcards.security.JwtService;
import com.bankcards.service.CardService;
import com.bankcards.service.TransferService;
import com.bankcards.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    @Autowired
    private JwtService jwtService;

    private void ensureUserAccess(long userId, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        long authenticatedUserId = Long.parseLong(authentication.getName());
        if (authenticatedUserId != userId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
    }


    //Получить пользователя по id
    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable Long id, Authentication authentication){
        ensureUserAccess(id, authentication);
        return userService.getUserDTO(id);
    }

    //Регистрация
    @PostMapping("/registration")
    public UserDTO registerUser(@Valid @RequestBody UserEntity userEntity){
        return userService.registerUser(userEntity);
    }

    //Авторизация
    @PostMapping("/authorization")
    public ResponseEntity<?> authorizeUser(@Valid @RequestBody UserLoginDTO login) {
        if (!userService.checkUser(login)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDTO user = userService.getUserDTOByEmail(login.getEmail());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        AuthResponseDTO authResponse = new AuthResponseDTO();
        authResponse.setUser(user);
        authResponse.setToken(jwtService.generateToken(user.getId(), user.getEmail()));
        return ResponseEntity.ok(authResponse);
    }

    //Обновление данных профиля
    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable long id, @Valid @RequestBody UpdateUserProfileDTO dto, Authentication authentication) {
        ensureUserAccess(id, authentication);
        return userService.updateUserProfile(id, dto);
    }

    //Возвращает карты и последние операции аккаунта
    @GetMapping("/{id}/summary")
    public AccountSummaryDTO getAccountSummary(@PathVariable long id, Authentication authentication) {
        ensureUserAccess(id, authentication);

        var cards = cardService.getCardsByUserId(id); // Возвращает List<CardDTO>
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
            r.setDescription(userIsSender ? "Перевод" : "Пополнение");
            r.setTransferTime(t.getTransferTime());

            return r;
        }).collect(Collectors.toList()));
        return dto;
    }
}
