package io.proj3ct.SpringDemoBot.DaO.MenuManaging.Adding;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.proj3ct.SpringDemoBot.DaO.MessageHandle;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.model.MenuRepository;
import io.proj3ct.SpringDemoBot.model.Vapecompony;
import io.proj3ct.SpringDemoBot.model.VapecomponyRepository;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class AddVapecompony_LastShtrich  implements MessageHandle {

    @Autowired
    private AddMenu_handler addMenu_handler;

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private VapecomponyRepository vapecomponyRepository;

    @Autowired
    private MessageRegistry messageRegistry;

    @Override
    public boolean support(org.telegram.telegrambots.meta.api.objects.Message msg) {
        return (addMenu_handler.get_new_vapecompony(msg.getFrom().getId())!=null);

    }

    @Override
    public void handle(Message msg, TelegramLongPollingBot bot) {


        Long bot_id = addMenu_handler.get_new_vapecompony(msg.getFrom().getId());
        Vapecompony vapecompony= new Vapecompony();

        vapecompony.setBot(botRepository.findById(bot_id).get());



        vapecompony.setName(msg.getText());
        try{
            ObjectMapper mapper = new ObjectMapper();
            String entitiesJson = mapper.writeValueAsString(msg.getEntities());
            vapecompony.setEntitiesJson(entitiesJson);

        }
        catch(JsonProcessingException e){
            e.printStackTrace();
        }


        vapecomponyRepository.save(vapecompony);
        addMenu_handler.clear(msg.getFrom().getId());

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(msg.getChatId().toString());

        if(vapecompony.getEntitiesJson()!=null){
            try {
                ObjectMapper mapper2 = new ObjectMapper();
                List<MessageEntity> entities = mapper2.readValue(
                        vapecompony.getEntitiesJson(),
                        new TypeReference<List<MessageEntity>>() {}
                );
                sendMessage.setEntities(entities);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        sendMessage.setText(vapecompony.getName() +"\n\n" + "<blockquote>Этот раздел — шаг на пути к товару.\nЗдесь клиент также увидит другие продукты</blockquote>\n\n"+"⚙ Внесите свои изминения");

        Vapecompony vape = vapecomponyRepository.findByNameAndBot_Id(vapecompony.getName(), bot_id).get();
        Long vapecompony_id = vape.getId();


        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(List.of(button("✏\uFE0F Название", "CHANGE_PODMENU_NAME:" + vapecompony_id + ":" + bot_id)));
        keyboard.add(List.of(button("📝 Описание", "CHANGE_PODMENU_DISCRIPTION:"+ vapecompony_id + ":" + bot_id)));
        keyboard.add(List.of(button("\uD83C\uDFAC Медиа", "CHANGE_PODMENU_MEDIA:"+ vapecompony_id + ":" + bot_id)));
        keyboard.add(List.of(button("❌ УДАЛИТЬ", "DELETE_MEDIA:"+ vapecompony_id + ":" + bot_id)));
        keyboard.add(List.of(button("\uD83D\uDD19 Назад", "EDITAGAIN:"+bot_id)));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboard);
        sendMessage.setReplyMarkup(markup);
        sendMessage.setParseMode("HTML");

        try {
          //  bot.execute(sendMessage);
            Message msgg =  bot.execute(sendMessage);
            messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
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
