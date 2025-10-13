package io.proj3ct.SpringDemoBot.DaO.BotCreating;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DB_entities.PlatformUser;
import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;

import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.repository.BotMessageRepository;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import io.proj3ct.SpringDemoBot.repository.PlatformUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
@Component

public class CreateBotCallbackhandle implements CallbackHandler {

    @Autowired
    private PlatformUserRepository userRepository;
    @Autowired
    private BotRepository botRepository;

    @Autowired
    private BotMessageRepository botMessageRepository;

    @Autowired
    private BotDefaultValues defaultValues;

    @Autowired
    private MessageRegistry messageRegistry;


    @Override
    public boolean support(String callbackData) {
        return "CREATE_BOT_EASILY".equals(callbackData);
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {



        Long telegramId = query.getFrom().getId();
        Optional<PlatformUser> userOpt = userRepository.findByTelegramId(telegramId);
        if(userOpt.isEmpty()) return;

        PlatformUser user = userOpt.get();

        Optional<Bot> freeBotOpt = botRepository.findFirstByOwnerIsNull();
        if(freeBotOpt.isEmpty()) {
            send(bot, query.getMessage().getChatId().toString(),
                    "⚠️ К сожалению, все шаблонные боты уже заняты.\n" +
                            "Попробуйте позже или обратитесь к администратору.");
            return;
        }

        Bot freeBot = freeBotOpt.get();
        freeBot.setOwner(user);
        freeBot.setSubscriptionStatus("free");
        freeBot.setCurrentPrice(BigDecimal.ZERO);
        freeBot.setRegistrationDate(LocalDateTime.now());
        freeBot.setPaymentDue(LocalDateTime.now().plusDays(7));
        freeBot.setActive(true);
        freeBot.setCart(true);
        freeBot.setNalichka(true);

        freeBot.create();
       // defaultValues.setDefault(freeBot);


        botRepository.save(freeBot);


        String botUsername = extractUsernameFromToken(freeBot.getBotToken());
        String link = "https://t.me/" + botUsername;

        String text =
        "🎉 Ваш бот успешно создан!\n\n" +
                "Запустите его тут:\n" + link + "\n\n" +
                "🔓 Пробный период: 7 дней.";

        InlineKeyboardButton goToBotButton = new InlineKeyboardButton("🚀 Открыть бота");
        goToBotButton.setUrl(link);

        InlineKeyboardButton editFunctionalityButton = new InlineKeyboardButton("⚙ Изменить функционал бота");
        editFunctionalityButton.setCallbackData("EDIT_BOT_FUNCTION:" + freeBot.getBotToken());



        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(Collections.singletonList(goToBotButton));
        keyboard.add(Collections.singletonList(editFunctionalityButton));


        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);

        SendMessage message = new SendMessage();
        message.setChatId(query.getMessage().getChatId().toString());
        message.setText(text);
        message.setReplyMarkup(markup);

        try {

            Message msg =  bot.execute(message);
            messageRegistry.addMessage(msg.getChatId(), msg.getMessageId());
        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    private void send(TelegramLongPollingBot bot, String chatId, String text) {
        try {
            Message msgg =
            bot.execute(new SendMessage(chatId, text));

            messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private String extractUsernameFromToken(String token) {

        Bot need_bot = botRepository.findByBotToken(token).get();
        return need_bot.getBotusername();

    }
}
