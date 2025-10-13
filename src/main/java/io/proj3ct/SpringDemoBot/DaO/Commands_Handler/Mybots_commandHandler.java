package io.proj3ct.SpringDemoBot.DaO.Commands_Handler;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DaO.CommandHandler;

import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class Mybots_commandHandler implements CommandHandler {

    @Autowired
    private BotRepository botRepository;
    @Autowired
    private MessageRegistry messageRegistry;




    @Override
    public boolean support(String command) {
        return command.equals("\uD83E\uDD16 Мои боты");
    }

    @Override
    public void handle(Message message, TelegramLongPollingBot bot) {

        //messageRegistry.deleteMessagesAfter(messageRegistry.getStartMessage().getChatId(), messageRegistry.getStartMessage().getMessageId(), false, bot);

        Long userId = message.getFrom().getId(); // Telegram user id
        Long chatId = message.getChatId();


        List<Bot> userBots = botRepository.findByOwner_TelegramId(userId);

        if (userBots.isEmpty()) {
            sendText(bot, chatId, "У вас пока нет созданных ботов.");
            return;
        }
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (Bot b : userBots) {
            String name = b.getName(); // Або короткий опис
            String callback = "SHOW_BOT_MENU:" + b.getId();
            rows.add(List.of(createBtn(name, callback)));
        }
        SendMessage msg = new SendMessage();
        msg.setParseMode("MarkDownV2");
        msg.setChatId(chatId.toString());

        String text = "*"+escapeMarkdown("\uD83E\uDD16") +"Вот все ваши боты:*" +"\n\n"+
                 "_"+
                "Нажмите на любой бот ниже, чтобы управлять его настройками, просматривать статистику или редактировать функционал_"+ "\n" +
                "\n" +
                escapeMarkdown("\uD83D\uDC47\uD83D\uDC47\uD83D\uDC47");
        msg.setText(text);

        markup.setKeyboard(rows);

        msg.setReplyMarkup(markup);

        try {
            Message msgg =  bot.execute(msg);

           // messageRegistry.getUp_to_salesUp().put(msgg.getChatId(), msgg.getMessageId());

            messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }


    }
    private void sendText(TelegramLongPollingBot bot, Long chatId, String text) {
        try {
           Message msgg=  bot.execute(new SendMessage(chatId.toString(), text));
            messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private InlineKeyboardButton createBtn(String text, String callbackData) {
        InlineKeyboardButton btn = new InlineKeyboardButton();
        btn.setText(text);
        btn.setCallbackData(callbackData);
        return btn;
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
