package com.bankcards.service;

import org.springframework.ai.chat.client.ChatClient;

public interface FinanceAssistantService {
    String FinanceAssistantServiceImpl(ChatClient.Builder builder);
    String analyzeSpending(Long userId, int limit);

}
