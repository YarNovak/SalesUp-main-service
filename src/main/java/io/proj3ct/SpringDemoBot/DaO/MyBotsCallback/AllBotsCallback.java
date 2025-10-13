package io.proj3ct.SpringDemoBot.DaO.MyBotsCallback;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class AllBotsCallback implements CallbackHandler {

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private MessageRegistry messageRegistry;


    @Override
    public boolean support(String callbackData) {

        return (callbackData.equals("MY_BOTS") || callbackData.equals("BACK_TO_BOTS"));
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {


        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);



        Long userId = query.getFrom().getId(); // Telegram user id
        Long chatId = query.getMessage().getChatId();


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

        //rows.add(List.of(createBtn("↩ Назад", "GO_TO_START")));

        if(query.getData().equals("BACK_TO_BOTS")){
            EditMessageText msg = new EditMessageText();
            msg.setParseMode("MarkDownV2");


            msg.setMessageId(query.getMessage().getMessageId());
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
                bot.execute(msg);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }
        else{

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
              //  bot.execute(msg);

                Message msgg =  bot.execute(msg);
               // messageRegistry.getUp_to_salesUp().put(msgg.getChatId(), msgg.getMessageId());
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
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
