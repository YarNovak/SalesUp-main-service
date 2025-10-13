package io.proj3ct.SpringDemoBot.DaO.ButtonEditing;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import io.proj3ct.SpringDemoBot.repository.PlatformUserRepository;
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
import java.util.Optional;

@Component
public class EditButtonsCallbackHandler implements CallbackHandler {

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private OwnerCheking cheking;
    @Autowired
    private PlatformUserRepository platformUserRepository;

    @Override
    public boolean support(String callbackData) {
        return callbackData.startsWith("EDIT_BUTTONS:");
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {

        String botId = callbackDataPart(query.getData(), "EDIT_BUTTONS:");
        if(!cheking.mustCheck(Long.parseLong(botId), query.getFrom().getId(), bot)){return;};

        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);


        Optional<Bot> botOpt = botRepository.findById(Long.parseLong(botId));
        if (botOpt.isEmpty()) return;

        SendMessage msg = new SendMessage();


        msg.setChatId(query.getMessage().getChatId().toString());
        msg.setParseMode("HTML");
        msg.setText("\uD83D\uDD18 Выберите кнопку, текст которой хотите изменить:\n\n" +
                "<i>Изменение коснется надписи на кнопке, которую видят ваши пользователи в меню или под сообщениями.</i>");

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(List.of(button("🛍 Каталог", "RENAME_BUTTON:catalog:" + botId)));
        keyboard.add(List.of(button("🛒 Корзина", "RENAME_BUTTON:cart:" + botId)));
        keyboard.add(List.of(button("\uD83D\uDCB8Оплата", "RENAME_BUTTON:payment:" + botId)));
        keyboard.add(List.of(button("\uD83D\uDD25в корзину", "RENAME_BUTTON:first_add:" + botId)));
        keyboard.add(List.of(button("❌Нету в наличии", "RENAME_BUTTON:empty_add:" + botId)));
        keyboard.add(List.of(button("✏\uFE0FИзменить", "RENAME_BUTTON:change:" + botId)));
        keyboard.add(List.of(button("➕ Прибавить", "RENAME_BUTTON:add:" + botId)));
        keyboard.add(List.of(button("➖ Уменьшить", "RENAME_BUTTON:delete:" + botId)));
        keyboard.add(List.of(button("\uD83D\uDDD1\uFE0FОчистить", "RENAME_BUTTON:clear:" + botId)));
        keyboard.add(List.of(button("\uD83D\uDECDКаталог", "RENAME_BUTTON:catalog:" + botId)));
        keyboard.add(List.of(button("\uD83D\uDCB5Наличка", "RENAME_BUTTON:cash_method:" + botId)));
        keyboard.add(List.of(button("\uD83D\uDECDБезнал", "RENAME_BUTTON:cart_method:" + botId)));
        keyboard.add(List.of(button("\uD83D\uDCF1Контакт", "RENAME_BUTTON:contact:" + botId)));


        keyboard.add(List.of(button("🔙 Назад", "TOTALLY_END")));

        msg.setReplyMarkup(new InlineKeyboardMarkup(keyboard));

        try {

            Message msgg =  bot.execute(msg);
            messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String callbackDataPart(String full, String prefix) {
        return full.substring(prefix.length());
    }

    private InlineKeyboardButton button(String text, String callbackData) {
        InlineKeyboardButton b = new InlineKeyboardButton(text);
        b.setCallbackData(callbackData);
        return b;
    }
}
