package io.proj3ct.SpringDemoBot.DaO.Commands_Handler;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DaO.CommandHandler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Component
public class Oplata_commandHandler implements CommandHandler {

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private BotRepository botRepository;

    @Override
    public boolean support(String command) {
        return command.equals("\uD83D\uDCB3 Оплата");
    }

    @Override
    public void handle(Message message, TelegramLongPollingBot bot) {

        messageRegistry.deleteMessagesAfter(message.getChatId(), message.getMessageId(), false, bot);

        Long userId = message.getFrom().getId();
        Long chatId = message.getChatId();

        List<Bot> userBots = botRepository.findByOwner_TelegramId(userId);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        if (userBots.isEmpty()) {

            sendText(bot, chatId, "У вас пока нет созданных ботов.");
            return;
        }

        for (Bot b : userBots) {
            String name = b.getName(); // Або короткий опис
            String callback = "P_or_N:" + b.getId();
            rows.add(List.of(createBtn(name, callback)));
        }


        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setParseMode("MarkDownV2");

        markup.setKeyboard(rows);
        sendMessage.setReplyMarkup(markup);



        sendMessage.setText(escapeMarkdown("✨")+" *Выберите бота*\n" +
                "_У вас всегда остаётся возможность:_\n" +
                "\n" +
                "> "+escapeMarkdown("\uD83D\uDCB3")+" Сделать оплату\n" +
                "> "+escapeMarkdown("\uD83D\uDEAB")+" Отменить\n" +
                "\n" +
                "Просто нажмите на кнопку ниже"+escapeMarkdown("\uD83E\uDD2B"));
/*
        String link ="https://www.youtube.com/";

        InlineKeyboardButton payButton = new InlineKeyboardButton();
        payButton.setText("💳 Оплатити");
        payButton.setUrl(link); // при натисканні відкриється сайт Gumroad

        rows.add(Collections.singletonList(payButton));
        markup.setKeyboard(rows);
        try {

            Message msgg = bot.execute(sendMessage);
            messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }
*/


        try {

            Message msgg = bot.execute(sendMessage);
            messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
        }
        catch (TelegramApiException e) {
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
