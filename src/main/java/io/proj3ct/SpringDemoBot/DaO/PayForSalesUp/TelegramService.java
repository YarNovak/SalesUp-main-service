package io.proj3ct.SpringDemoBot.DaO.PayForSalesUp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Map;

@Service
public class TelegramService {

    @Value("${bot.token}")
    private String botToken;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendMessage(Long chatId, String text) {
        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";
        restTemplate.postForObject(url, Map.of(
                "chat_id", chatId,
                "text", text
        ), String.class);

    }


}
