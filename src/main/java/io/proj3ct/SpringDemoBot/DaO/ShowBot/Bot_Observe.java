package io.proj3ct.SpringDemoBot.DaO.ShowBot;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DB_entities.PlatformUser;
import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import io.proj3ct.SpringDemoBot.repository.PlatformUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class Bot_Observe implements CallbackHandler {

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private PlatformUserRepository userRepository;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private OwnerCheking cheking;

    @Override
    public boolean support(String callbackData) {


        return ((callbackData.startsWith("SHOW_BOT_MENU:")) || callbackData.startsWith("BACK_TO_BOT:") );
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {



        Long userId = query.getFrom().getId(); // Telegram user id
        Long chatId = query.getMessage().getChatId();

        String parts[] = query.getData().split(":");
        long botId = Long.parseLong(parts[1]);

        if(!cheking.mustCheck(botId, query.getMessage().getChatId(), bot)){return;};
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);

        System.out.println(parts[0]+parts[1]);

        Optional<Bot> botOpt = botRepository.findByIdAndOwner_TelegramId(botId, userId);
        if (botOpt.isPresent()) {
            System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");

            Optional<PlatformUser> userOpt = userRepository.findByTelegramId(userId);
            if(userOpt.isEmpty()) return;


            PlatformUser user = userOpt.get();
            Bot freeBot = botOpt.get();

            String botUsername = extractUsernameFromToken(freeBot.getBotToken());
            String link = "https://t.me/" + botUsername;

            String text =
                   escapeMarkdown("🎉")+ " Ваш бот " + escapeMarkdown("\uD83D\uDC47")+"\n\n" +

                           "     "+  escapeMarkdown("✏\uFE0F")+ "меняйте функционал\n" +
                           "     "+  escapeMarkdown("⚙\uFE0F")+ "управляйте настройками\n" +
                           "     "+ escapeMarkdown("\uD83D\uDCC8")+ "смотрите статистику\n\n"+



                            "начните управлять своим бизнесом прямо сейчас:\n" + escapeMarkdown(link) + "\n\n" +escapeMarkdown("🔓")+
                            " Пробний период: 7 дней"+escapeMarkdown(".");

            InlineKeyboardButton goToBotButton = new InlineKeyboardButton("🚀 Открыть бота");
            goToBotButton.setUrl(link);

            InlineKeyboardButton editFunctionalityButton = new InlineKeyboardButton("⚙ Изменить функционал бота");
            editFunctionalityButton.setCallbackData("EDIT_BOT_FUNCTION:" + freeBot.getBotToken());

            InlineKeyboardButton statystyks = new InlineKeyboardButton("\uD83D\uDCC8 Статистика");
            statystyks.setCallbackData("Statystyks:" + freeBot.getBotToken());

            InlineKeyboardButton oplata = new InlineKeyboardButton("\uD83D\uDCB3 Оплата");
            oplata.setCallbackData("P_or_N:" + freeBot.getId());

            InlineKeyboardButton nazad = new InlineKeyboardButton("\uD83D\uDD19 Назад");
            nazad.setCallbackData("BACK_TO_BOTS");

            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            keyboard.add(Collections.singletonList(goToBotButton));
            keyboard.add(Collections.singletonList(editFunctionalityButton));
            keyboard.add(Collections.singletonList(statystyks));
            keyboard.add(Collections.singletonList(oplata));
            keyboard.add(Collections.singletonList(nazad));

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            markup.setKeyboard(keyboard);

            EditMessageText message = new EditMessageText();
            message.setParseMode("MarkDownV2");

            message.setMessageId(query.getMessage().getMessageId());

            message.setChatId(query.getMessage().getChatId().toString());
            message.setText(text);
            message.setReplyMarkup(markup);

            try {
                bot.execute(message);
            }
            catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }



    }

    private String extractUsernameFromToken(String token) {

        Bot need_bot = botRepository.findByBotToken(token).get();
        return need_bot.getBotusername();
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
