package io.proj3ct.SpringDemoBot.DaO.Products_manipulation.MyProducts;

import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import io.proj3ct.SpringDemoBot.model.Vapecompony;
import io.proj3ct.SpringDemoBot.model.VapecomponyRepository;
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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class WhereProductsAre implements CallbackHandler {

    @Autowired
    private VapecomponyRepository vapecomponyRepository;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private OwnerCheking cheking;

    @Override
    public boolean support(String callbackData) {
        return callbackData.startsWith("PRODUCTS_OBSERVE:");
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {



        String[] parts = query.getData().split(":");
        Long botId = Long.parseLong(parts[1]);

        if(!cheking.mustCheck(botId, query.getMessage().getChatId(), bot)){return;};
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);

        send_products_repository(query, query.getMessage().getChatId(), botId, bot);

    }

    private void send_products_repository(CallbackQuery query,Long chatId, Long botId, TelegramLongPollingBot bot) {

        EditMessageText sendMessage = new EditMessageText();
        sendMessage.setMessageId(query.getMessage().getMessageId());
        sendMessage.setChatId(query.getMessage().getChatId().toString());
        sendMessage.setParseMode("MarkDownV2");

        String txt = escapeMarkdown("\uD83C\uDFAF")+"Снизу — категории товаров\n" +
                "\n" +"> "+
                escapeMarkdown("\uD83E\uDDD1\u200D\uD83D\uDCBB")+"Нажмите на нужную категорию, чтобы увидеть и управлять продуктами внутри"+escapeMarkdown("\uD83D\uDC4C")+"\n" +
                "\n" +
                escapeMarkdown("\uD83D\uDC47\uD83D\uDC47\uD83D\uDC47");

        sendMessage.setText(txt);

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();


        long k = 0;
        List<Vapecompony> vcr = StreamSupport.stream(vapecomponyRepository.findAllByBot_Id(Long.valueOf(botId)).spliterator(), false)
                .collect(Collectors.toList());

        while(vcr.size()>0){


            List<InlineKeyboardButton> rowInLine = new ArrayList<>();

            var yesButton = new InlineKeyboardButton();

            vcr.get(0).getName();
            yesButton.setText(vcr.get(0).getName());
            yesButton.setCallbackData("SHOW_ALL_FROMMENU:"+vcr.get(0).getId()+":"+botId);
            vcr.remove(0);
            rowInLine.add(yesButton);

            rowsInLine.add(rowInLine);

        }

        List<InlineKeyboardButton> rowwInLine = new ArrayList<>();

        var Button = new InlineKeyboardButton();

        Button.setText("\uD83D\uDD19 Назад");
        Button.setCallbackData("EDITEDIT_PRODUCT:"+botId);
        rowwInLine.add(Button);

        rowsInLine.add(rowwInLine);

        markupInLine.setKeyboard(rowsInLine);
        sendMessage.setReplyMarkup(markupInLine);

        try{

            bot.execute(sendMessage);

        }
        catch (TelegramApiException e){
            e.printStackTrace();
        }
    }
    private void executeMessage(SendMessage message, TelegramLongPollingBot bot) {
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
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
