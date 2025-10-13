package io.proj3ct.SpringDemoBot.DaO;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class SettingsChangeingCallbackhandle implements CallbackHandler {


    @Autowired
    BotRepository botRepository;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private OwnerCheking cheking;

    @Override
    public boolean support(String callbackData) {
        return callbackData != null && callbackData.startsWith("EDIT_BOT_FUNCTION:");
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {



        String botToken = query.getData().replace("EDIT_BOT_FUNCTION:", "");
        Optional<Bot> bot_opt = botRepository.findByBotToken(botToken);
        String botUsername = bot_opt.get().getBotusername();
        if(bot_opt.isEmpty()) { return ;}
        Bot my_bot = bot_opt.get();
        Long bot_id = my_bot.getId();

        if(!cheking.mustCheck(bot_id, query.getMessage().getChatId(), bot)){return;};
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);

        String text;
        text = escapeMarkdown("⚡\uFE0F") +  "Настройте свой бизнес" + escapeMarkdown("!")+ "\n" +
                "\n" +
                "> `" +"   "+escapeMarkdown("\uD83D\uDD18")+ "Изменяйте кнопки`\n" +
                "> \n" +
                "> `"+"   "+escapeMarkdown("\uD83D\uDCAC") + "персонализируйте сервис `\n" +
                "> \n" +
                "> `"+"   "+escapeMarkdown("\uD83D\uDECD")
                 + "Меняйте главное меню`\n" +
                "> \n" + "> `"+"   "+escapeMarkdown("\uD83C\uDFF7")
                 + "управляйте продажами`" +"\n\n"+
                "\n" +
                "*SalesUp* делает управление вашим бизнесом простым и удобным" + escapeMarkdown("\uD83D\uDE09");





        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(List.of(button("🔘 Название кнопок", "EDIT_BUTTONS:" + bot_id)));
        keyboard.add(List.of(button("\uD83D\uDCAC Сообщения", "EDIT_MESSAGES:" + bot_id)));
        keyboard.add(List.of(button("\uD83D\uDECD Меню", "EDIT_MENU:" + bot_id)));
        keyboard.add(List.of(button("\uD83C\uDFF7 Продукты и услуги", "EDIT_PRODUCT:" + bot_id)));
        keyboard.add(List.of(button("\uD83D\uDCB2 Валюта", "EDIT_CURRENCY:" + bot_id)));
        keyboard.add(List.of(button("\uD83D\uDD19 Назад", "BACK_TO_BOT:" + bot_id)));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboard);

        EditMessageText message = new EditMessageText();
        message.setMessageId(query.getMessage().getMessageId());
        message.setChatId(query.getMessage().getChatId().toString());
        message.setParseMode("MarkDownV2");
        message.setText(text);
        message.setReplyMarkup(markup);

        try{
            bot.execute(message);

            messageRegistry.getUp_to_salesUp().put(Long.valueOf(message.getChatId()), message.getMessageId());

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
