package io.proj3ct.SpringDemoBot.DaO.BotCreating;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DB_entities.PlatformUser;
import io.proj3ct.SpringDemoBot.DaO.CommandHandler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import io.proj3ct.SpringDemoBot.repository.PlatformUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
public class BotCreating_commandHandler implements CommandHandler {

    @Autowired
    private PlatformUserRepository userRepository;
    @Autowired
    private BotRepository botRepository;
    @Autowired
    private BotDefaultValues defaultValues;

    @Autowired
    private MessageRegistry messageRegistry;

    @Override
    public boolean support(String command) {
        return command.equals("\uD83D\uDE80 Создать бота");
    }

    @Override
    public void handle(Message m, TelegramLongPollingBot bot) {


        SendMessage message = new SendMessage();
        message.setChatId(m.getChatId().toString());
        message.setText("Как вы хотите создать бота?\n" + //
                        "\n" + //
                        "⚡Один клик — мы всё сделаем за вас.\n" + //
                        "🔑свой токен — вставьте токен из @BotFather");


        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(List.of(button("⚡Один клик", "CREATE_BOT_EASILY"),
                button("🔑 свой токен", "CREATE_BOTFATHER")));


        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboard);

        message.setReplyMarkup(markup);

        try{

            Message msg =  bot.execute(message);
            messageRegistry.addMessage(msg.getChatId(), msg.getMessageId());
        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    private InlineKeyboardButton button(String text, String key) {

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(key);
        return button;
        }
}
/*
        Long telegramId = message.getFrom().getId();
        Optional<PlatformUser> userOpt = userRepository.findByTelegramId(telegramId);
        if(userOpt.isEmpty()) return;

        PlatformUser user = userOpt.get();
        System.out.println("{}");

        System.out.println("{}");


        Optional<Bot> freeBotOpt = botRepository.findFirstByOwnerIsNull();
        if(freeBotOpt.isEmpty()) {
            send(bot, message.getChatId().toString(),
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

        InlineKeyboardButton editFunctionalityButton = new InlineKeyboardButton("⚙ Измените функционал бота");
        editFunctionalityButton.setCallbackData("EDIT_BOT_FUNCTION:" + freeBot.getBotToken());



        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(Collections.singletonList(goToBotButton));
        keyboard.add(Collections.singletonList(editFunctionalityButton));


        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);

        SendMessage messageg = new SendMessage();
        messageg.setChatId(message.getChatId().toString());
        messageg.setText(text);
        messageg.setReplyMarkup(markup);

        try {

            Message msg =  bot.execute(messageg);
            messageRegistry.addMessage(msg.getChatId(), msg.getMessageId());
        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    private void send(TelegramLongPollingBot bot, String chatId, String text) {
        try {

            Message msgg = bot.execute(new SendMessage(chatId, text));
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
*/