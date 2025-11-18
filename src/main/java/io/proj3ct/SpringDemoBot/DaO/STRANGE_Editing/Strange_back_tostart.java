package io.proj3ct.SpringDemoBot.DaO.STRANGE_Editing;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DB_entities.PlatformUser;
import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.config.BotConfig;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import io.proj3ct.SpringDemoBot.repository.PlatformUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class Strange_back_tostart implements CallbackHandler {
    @Autowired
    private PlatformUserRepository platformUserRepository;
    @Autowired
    private BotRepository botRepository;
    @Autowired
    private BotConfig config;

    @Override
    public boolean support(String command) {

        return "GO_TO_START".equals(command);
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {

        Long telegamId = query.getMessage().getFrom().getId();
        String username = query.getMessage().getFrom().getUserName();
        String welcometext;
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        Optional<PlatformUser> existingUser = platformUserRepository.findByTelegramId(telegamId);
        EditMessageText sendMessage = new EditMessageText();
        sendMessage.setMessageId(query.getMessage().getMessageId());

        if(existingUser.isEmpty()){

            PlatformUser platformUser = new PlatformUser();
            platformUser.setTelegramId(telegamId);
            platformUser.setUsername(username);
            platformUser.setRegistrationDate(LocalDateTime.now());
            platformUser.setDiscountStage(0);
            platformUser.setTotalReferrals(0);

            platformUserRepository.save(platformUser);
            welcometext =escapeMarkdown("👋") + " Приветствуем" +  (username != null ? ", "+ escapeMarkdown("@" )+ escapeMarkdown(username) : "user")+escapeMarkdown("!")+
                    "\n\n" +
                    "Добро пожаловать в " +
                    "*__SALESUP" +escapeMarkdown("!")+ "__*"  +"\n\n"
                    +
                    "> " + "*SalesUp* "+ escapeMarkdown(" — ваш персональный помощник в продажах и управлении клиентами.");

            List<List<InlineKeyboardButton>> rows = new ArrayList<>();

            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText("\uD83D\uDE80 Создать бота");
            button.setCallbackData("CREATE_BOT");

            rows.add(Collections.singletonList(button));

            markup.setKeyboard(rows);
            sendMessage.setReplyMarkup(markup);


        }
        else{

            PlatformUser platformUser = existingUser.get();
            List<Bot> userBots = botRepository.findByOwner(platformUser);
            welcometext = escapeMarkdown("👋") + " Приветствуем" +  (username != null ? ", "+ escapeMarkdown("@" )+ escapeMarkdown(username) : "user")+escapeMarkdown("!")+
                    "\n\n" +
                    "Добро пожаловать в " +
                    "*__SALESUP" +escapeMarkdown("!")+ "__*"  +"\n\n"
                    +
                    "> " + "*SalesUp* "+ escapeMarkdown(" — ваш персональный помощник в продажах и управлении клиентами через в Telegram");


            List<List<InlineKeyboardButton>> rows = new ArrayList<>();


            if(!userBots.isEmpty()){
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText("\uD83E\uDD16 Мои боты");
                button.setCallbackData("MY_BOTS");
                rows.add(Collections.singletonList(button));
            }


            InlineKeyboardButton button2 = new InlineKeyboardButton();
            button2.setText("\uD83D\uDE80 Создать бота");
            button2.setCallbackData("CREATE_BOT");


            rows.add(Collections.singletonList(button2));
            markup.setKeyboard(rows);
            sendMessage.setReplyMarkup(markup);


        }



        sendMessage.setChatId(query.getMessage().getChatId().toString());
        sendMessage.setParseMode("MarkDownV2");
        sendMessage.setText(welcometext);
        // sendMessage.setReplyMarkup(getMainKeyboard());




        try{
      //      bot.execute(hello_there);
            bot.execute(sendMessage);
        }
        catch (TelegramApiException e){
            e.printStackTrace();
        }

    }

    public ReplyKeyboardMarkup getMainKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true); // Підганяє під розмір екрана
        keyboardMarkup.setOneTimeKeyboard(false); // Залишається після натискання

        // Один рядок з трьома кнопками
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("\uD83D\uDE80 Создать бота"));
        row.add(new KeyboardButton("\uD83D\uDCB3 Оплата"));
        row.add(new KeyboardButton("\uD83E\uDD16 Мои боты"));

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);

        return keyboardMarkup;
    }

    public void printBotInfo(TelegramLongPollingBot bot) {
        try {
            User me = bot.execute(new GetMe());
            System.out.println("Bot name: " + me.getFirstName());
            System.out.println("Bot username: @" + me.getUserName());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private String escapeMarkdown(String text) {
        return text.replace("\\", "\\\\")  // Екрануємо зворотні слеші
                .replace("_", "\\_")
                .replace("*", "\\*")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("~", "\\~")
                .replace("`", "\\`")
                .replace(">", "\\>")
                .replace("#", "\\#")
                .replace("+", "\\+")
                .replace("-", "\\-")
                .replace("=", "\\=")
                .replace("|", "\\|")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace(".", "\\.")
                .replace("!", "\\!");
    }
}
