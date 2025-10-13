package io.proj3ct.SpringDemoBot.service;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.*;

public class MyTelegramBot extends TelegramLongPollingBot {

    private static final String BOT_USERNAME = "SpotLab_bot"; // 🔹 Введи свій юзернейм
    private static final String BOT_TOKEN = "7936063155:AAFjN1y0XIA9gjptzHQ0Rg29QpiRRbZRvVQ";       // 🔹 Введи свій токен

    // Список користувачів, які ще не натиснули "Почати"
    private final Set<Long> pendingUsers = new HashSet<>();

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();

            // Якщо це перший контакт – відправляємо повідомлення та блокуємо команди
            if (!pendingUsers.contains(chatId)) {
                sendWelcomeMessage(chatId);
                pendingUsers.add(chatId);
            }
            // Дозволяємо тільки "Почати"
            else if (messageText.equals("🚀 Почати")) {
                allowUserCommunication(chatId);
                pendingUsers.remove(chatId); // Видаляємо користувача зі списку
            }
            // Якщо користувач спробує відправити щось інше
            else {
                remindUserToPressStart(chatId);
            }
        }
    }

    // 📌 Надсилаємо центральне повідомлення з кнопкою
    private void sendWelcomeMessage(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("👋 *Вітаємо у боті!*\n\n" +
                "📢 Перед використанням прочитайте інструкцію.\n" +
                "✅ Натисніть кнопку '🚀 Почати', щоб продовжити.");
        message.setParseMode("Markdown");

        // Додаємо кнопку "Почати"
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        KeyboardRow row = new KeyboardRow();
        row.add("🚀 Почати");

        keyboardMarkup.setKeyboard(Collections.singletonList(row));
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 📌 Якщо натиснули "Почати" – дозволяємо взаємодію
    private void allowUserCommunication(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("✅ Тепер ви можете користуватися ботом!");
        message.setReplyMarkup(null); // Прибираємо клавіатуру

        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 📌 Якщо користувач вводить щось інше – нагадуємо натиснути кнопку
    private void remindUserToPressStart(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("⚠ *Натисніть кнопку '🚀 Почати', щоб продовжити!*");
        message.setParseMode("Markdown");

        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 📌 Запуск бота
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            // botsApi.registerBot(new MyTelegramBot());
            System.out.println("🤖 Бот запущено!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
