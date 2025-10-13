package io.proj3ct.SpringDemoBot.DaO.Products_manipulation.MyProducts;

import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import io.proj3ct.SpringDemoBot.model.VapecomponyKatalogRepository;
import io.proj3ct.SpringDemoBot.model.Vapecompony_katalog;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;
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
public class ShowProductFrom implements CallbackHandler {

    @Autowired
    private VapecomponyKatalogRepository vapecomponyKatalogRepository;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private OwnerCheking cheking;

    @Override
    public boolean support(String callbackData) {
        return callbackData.startsWith("SHOW_ALL_FROMMENU");
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {




        String[] parts = query.getData().split(":");
        Long vapecompony_id = Long.parseLong(parts[1]);
        Long botId = Long.parseLong(parts[2]);


        if(!cheking.mustCheck(botId, query.getMessage().getChatId(), bot)){return;};
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);

        EditMessageText sendMessage = new EditMessageText();
        sendMessage.setParseMode("HTML");
        sendMessage.setMessageId(query.getMessage().getMessageId());
        sendMessage.setChatId(query.getMessage().getChatId().toString());
        sendMessage.setText("\uD83D\uDCC2 Внизу — товары из выбранной категории.\n" +
                "\n" +
                "✏\uFE0F <i>Выберите продукт для изменений</i> \uD83D\uDC47");

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        List<Vapecompony_katalog> vk = vapecomponyKatalogRepository.findByVapecompony_idAndBot_Id(vapecompony_id, botId);
        for(Vapecompony_katalog vcr : vk) {

            List<InlineKeyboardButton> rowInLine = new ArrayList<>();

            var yesButton = new InlineKeyboardButton();

            yesButton.setText(vcr.getName());
            yesButton.setCallbackData("LETS_UPDATE:"+vcr.getId()+":"+botId);

            rowInLine.add(yesButton);

            rowsInLine.add(rowInLine);

        }

        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        var yesButton = new InlineKeyboardButton();

        yesButton.setText("\uD83D\uDD19 Назад");
        yesButton.setCallbackData("PRODUCTS_OBSERVE:"+botId);

        rowInLine.add(yesButton);

        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        sendMessage.setReplyMarkup(markupInLine);

        try{

            bot.execute(sendMessage);

        }
        catch (TelegramApiException e) {
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
