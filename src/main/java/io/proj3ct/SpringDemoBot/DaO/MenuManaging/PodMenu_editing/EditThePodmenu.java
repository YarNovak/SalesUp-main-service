package io.proj3ct.SpringDemoBot.DaO.MenuManaging.PodMenu_editing;

import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.DaO.MessageHandle;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import io.proj3ct.SpringDemoBot.model.Vapecompony;
import io.proj3ct.SpringDemoBot.model.VapecomponyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;


@Component
public class EditThePodmenu implements CallbackHandler {

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private VapecomponyRepository vapecomponyRepository;

    @Autowired
    private OwnerCheking cheking;

    @Override
    public boolean support(String callbackData) {
        return callbackData.startsWith("EDIT_PODMENU");
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {



        String[] parts = query.getData().split(":");
        Long vapecompony_id = Long.parseLong(parts[1]);
        Long botId = Long.parseLong(parts[2]);
        System.out.println(vapecompony_id + " " +botId);

        if(!cheking.mustCheck(botId, query.getMessage().getChatId(), bot)){return;};
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);

        Vapecompony vape = vapecomponyRepository.findByIdAndBot_Id(vapecompony_id, botId).get();


        String text =vape.getName()  +"\n\n" + "<blockquote>Этот раздел — шаг на пути к товару.\nЗдесь клиент также увидит другие продукты</blockquote>\n\n"+"⚙ Внесите свои изминения";

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(List.of(button("✏\uFE0F Название", "CHANGE_PODMENU_NAME:" + vapecompony_id + ":" + botId)));
        keyboard.add(List.of(button("📝 Описание", "CHANGE_PODMENU_DISCRIPTION:"+ vapecompony_id + ":" + botId)));
        keyboard.add(List.of(button("\uD83C\uDFAC Медиа", "CHANGE_PODMENU_MEDIA:"+ vapecompony_id + ":" + botId)));
        keyboard.add(List.of(button("❌ УДАЛИТЬ", "DELETE_MEDIA:"+ vapecompony_id + ":" + botId)));
        keyboard.add(List.of(button("\uD83D\uDD19 Назад", "EDITAGAIN:"+botId)));


        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboard);

        EditMessageMedia message = new EditMessageMedia();
        ;
        message.setMessageId(query.getMessage().getMessageId());

        message.setChatId(query.getMessage().getChatId().toString());

        InputMediaPhoto newPhoto = new InputMediaPhoto();
        newPhoto.setParseMode("HTML");
        newPhoto.setMedia("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/MenuPTV.png");


        newPhoto.setCaption(text);

        message.setReplyMarkup(markup);
        message.setMedia(newPhoto);

        try{
            bot.execute(message);
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
