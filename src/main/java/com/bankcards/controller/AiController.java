package com.bankcards.controller;

import com.bankcards.service.FinanceAssistantServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {
    private final FinanceAssistantServiceImpl aiService;

    public AiController(FinanceAssistantServiceImpl aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/analyze/{userId}")
    public ResponseEntity<Map<String, String>> analyze(@PathVariable Long userId) {
        String analysis = aiService.analyzeSpending(userId, 10);
        return ResponseEntity.ok(Map.of("result", analysis));
    }

}
