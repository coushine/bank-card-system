package com.bankcards.service;

import com.bankcards.repository.TransferRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FinanceAssistantServiceImpl {
    private final ChatClient chatClient;
    @Autowired
    private TransferService transferService;

    public FinanceAssistantServiceImpl(ChatClient.Builder builder) {
        // Настраиваем базовый системный промпт для финансового ассистента
        this.chatClient = builder
                .defaultSystem("Ты — помощник в банковском приложении. " +
                        "Отвечай четко и только по делу. Если данных недостаточно, уточняй.")
                .build();
    }

    public String analyzeSpending(Long userId, int limit) {
        // Получаем реальные данные из вашей БД
        var transactions = transferService.getRecentTransfersByUserId(userId, limit);

        // Формируем запрос для AI
        String prompt = "Вот список транзакций пользователя: " + transactions.toString() +
                ". Проанализируй эти транзакции на предмет подозрительной активности. Не пиши названия переменных в ответе. Не пиши id карты и id транзакции. Ответ должен быть четким и структурированным";

        var options = OpenAiChatOptions.builder()
                .model("llama-3.1-8b-instant") // Теперь просто .model() вместо .withModel()
                .temperature(0.2)
                .build();

        return chatClient.prompt(prompt)
                .options(options)
                .call()
                .content();
    }
}
