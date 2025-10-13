package io.proj3ct.SpringDemoBot.DaO.MenuManaging.Deleting;


import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;

import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import io.proj3ct.SpringDemoBot.model.*;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class DeleteMedia implements CallbackHandler {

    @Autowired
    private VapecomponyRepository vapecomponyRepository;

    @Autowired
    private VapecomponyKatalogRepository vapecomponyKatalogRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private OwnerCheking cheking;


    @Override
    public boolean support(String callbackData) {
        return callbackData.startsWith("DELETE_MEDIA:");
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {




        String[] parts = query.getData().split(":");
        Long vapecompony_id = Long.parseLong(parts[1]);
        Long botId = Long.parseLong(parts[2]);

        if(!cheking.mustCheck(botId, query.getMessage().getChatId(), bot)){return;};

        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);

        DeleteMessage dlt = new DeleteMessage();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(query.getMessage().getChatId()));

        Optional<Vapecompony> v = vapecomponyRepository.findByIdAndBot_Id(vapecompony_id, botId);
        if (v.isPresent()) {

            List<Vapecompony_katalog> vk = vapecomponyKatalogRepository.findByVapecompony_idAndBot_Id(vapecompony_id, botId);

            for (Vapecompony_katalog vk1 : vk) {

                List<CartItem> ck = cartItemRepository.findAllByVapecomponyKatalog_IdAndBot_Id(vk1.getId(), botId);
                cartItemRepository.deleteAll(ck);
            }


            vapecomponyKatalogRepository.deleteAll(vk);
            vapecomponyRepository.delete(v.get());


            dlt.setChatId(String.valueOf(query.getMessage().getChatId()));
            dlt.setMessageId(query.getMessage().getMessageId());


            sendMessage.setText("Text has been deleted))");

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();

            InlineKeyboardButton button = new InlineKeyboardButton();


            rows.add(Collections.singletonList(button));

            markup.setKeyboard(rows);
            sendMessage.setReplyMarkup(markup);


        }
        else{

            sendMessage.setText("Text has doesn't exist))");
        }

        try{
          bot.execute(dlt);

            // Message msgg =  bot.execute(sendMessage);
            //messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

           // bot.execute(sendMessage);
        }
        catch(TelegramApiException e){
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
