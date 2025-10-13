package io.proj3ct.SpringDemoBot.DaO.MenuManaging.PodMenu_changeszczegoly.opys;


import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.DaO.MenuManaging.HEPLING_SERVICES.Description.MenuDescription_handler;
import io.proj3ct.SpringDemoBot.DaO.MessageHandle;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class ChangeOpys implements CallbackHandler {

    @Autowired
    private MenuDescription_handler menuDescription_handler;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private OwnerCheking cheking;

    @Override
    public boolean support(String callbackData) {
        return callbackData.startsWith("SET_VAPEDESC_CHNG:");
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {


        String[] parts = query.getData().split(":");

        Long vapecompony_id = Long.parseLong(parts[1]);
        Long botId = Long.parseLong(parts[2]);

        if(!cheking.mustCheck(botId, query.getMessage().getChatId(), bot)){return;};
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(query.getMessage().getChatId()));
        sendMessage.setText("✏\uFE0FОпишите своё видение этого меню:");

        menuDescription_handler.expectDescriptionRename(query.getFrom().getId(), botId, vapecompony_id);
        try {
          //  bot.execute(sendMessage);

            Message msgg =  bot.execute(sendMessage);
            messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
        }
        catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }
}
