package io.proj3ct.SpringDemoBot.DaO.Admin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import io.proj3ct.SpringDemoBot.service.WebhookService;

@Component
public class RemoveBotHandler implements AdminHandler {

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private WebhookService webhookService;

    @Override
    public boolean supportAdmin(Message msgcallbackData) {
        return msgcallbackData.getText().startsWith("/rmbot");
    }

    @Override
    public void handle(Message message, TelegramLongPollingBot bot) {
        String text = message.getText();
        String payload = text.replaceFirst("/rmbot", "").trim();

        if (payload.isEmpty()) {
            String usage = "Использование: /rmbot <token_or_username1>,<token_or_username2>,...\nПример: /rmbot 123:ABC,@mybot";
            try {
                bot.execute(new SendMessage(message.getChatId().toString(), usage));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return;
        }

        String[] items = payload.replace(" ", "").split(",");
        List<String> removed = new ArrayList<>();
        List<String> notFound = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (String item : items) {
            if (item.isEmpty()) continue;

            Bot botEntity = null;

            // treat @username or username
            String lookup = item.startsWith("@") ? item.substring(1) : item;

            // First try to find by token
            var byToken = botRepository.findByBotToken(item);
            if (byToken.isPresent()) {
                botEntity = byToken.get();
            } else {
                // try by username
                var byUsername = botRepository.findByBotusername(lookup);
                if (byUsername.isPresent()) {
                    botEntity = byUsername.get();
                }
            }

            if (botEntity == null) {
                notFound.add(item);
                continue;
            }

            if (botEntity.getOwner() != null) {
                errors.add("owner_bot:" + item + " -> бот привязан к владельцу");
                continue;
            }

            // Attempt to delete webhook first (best-effort)
            try {
                webhookService.deleteTenantWebhook(botEntity.getBotToken());
            } catch (Exception e) {
                // record but continue with deletion
                errors.add("webhook:" + item + " -> " + e.getMessage());
            }

            try {
                botRepository.delete(botEntity);
                removed.add(item);
            } catch (Exception e) {
                errors.add("delete:" + item + " -> " + e.getMessage());
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Удалено: ").append(removed.size()).append("\n");
        for (String s : removed) sb.append("- ").append(s).append("\n");

        sb.append("\nНе найдено: ").append(notFound.size()).append("\n");
        for (String s : notFound) sb.append("- ").append(s).append("\n");

        if (!errors.isEmpty()) {
            sb.append("\nОшибки: ").append(errors.size()).append("\n");
            for (String e : errors) sb.append("- ").append(e).append("\n");
        }

        try {
            bot.execute(new SendMessage(message.getChatId().toString(), sb.toString()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
