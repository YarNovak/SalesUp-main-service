package io.proj3ct.SpringDemoBot.DaO.Admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.service.BotService;
import io.proj3ct.SpringDemoBot.service.OnboardResult;

@Component
public class OnboardBotsHandler implements AdminHandler {
    @Autowired
    private BotService botService;
    @Override
    public boolean supportAdmin(Message msgcallbackData) {
        return msgcallbackData.getText().startsWith("/ntoken");
    }

    @Override
    public void handle(Message message, TelegramLongPollingBot bot) {
        String text = message.getText();
        List<String> tokens = List.of(text.replace("/ntoken", "").trim().replace(" ", "").split(","));
        // If no tokens provided, send usage message
        if (tokens.isEmpty() || tokens.get(0).isEmpty()) {
            String usage = "Использование: /ntoken <token1>,<token2>,...\n\nПример: /ntoken 123:ABC,456:DEF";
            try {
                bot.execute(new SendMessage(message.getChatId().toString(), usage));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return;
        }

        // Onboard provided tokens and collect result
        OnboardResult result = botService.onboardBots(tokens);

        StringBuilder sb = new StringBuilder();

        // Created
        List<Bot> created = result.getCreated();
        sb.append("Создано: ").append(created.size()).append("\n");
        for (Bot b : created) {
            sb.append("- ").append(b.getName() == null ? "(без имени)" : b.getName());
            if (b.getBotusername() != null && !b.getBotusername().isEmpty()) {
                sb.append(" (@").append(b.getBotusername()).append(")");
            }
            sb.append("\n");
        }

        // Existed
        List<String> existed = result.getExisted();
        sb.append("\nУже существовали: ").append(existed.size()).append("\n");
        for (String t : existed) {
            sb.append("- ").append(t).append("\n");
        }

        // Not valid
        List<String> notValid = result.getNotValid();
        sb.append("\nНевалидные токены: ").append(notValid.size()).append("\n");
        for (String t : notValid) {
            sb.append("- ").append(t).append("\n");
        }

        try {
            bot.execute(new SendMessage(message.getChatId().toString(), sb.toString()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
