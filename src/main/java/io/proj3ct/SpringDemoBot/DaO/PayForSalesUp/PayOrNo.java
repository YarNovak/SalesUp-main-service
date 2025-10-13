package io.proj3ct.SpringDemoBot.DaO.PayForSalesUp;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class PayOrNo implements CallbackHandler {

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private BotRepository botRepository;

    @Override
    public boolean support(String callbackData) {
        return callbackData.startsWith("P_or_N:");
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {

        String parts[] = query.getData().split(":");

        Long bot_id = Long.valueOf(parts[1]);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

         Optional<Bot> botus = botRepository.findById(bot_id);
        if(botus.isEmpty()) {
            System.out.println("tuz");return;}

        long botId = Long.parseLong(parts[1]);
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(query.getMessage().getChatId().toString());
        sendMessage.setParseMode("MarkDownV2");
        sendMessage.setText(escapeMarkdown("\uD83D\uDE0B")+"*Стоимость бота — 15$ / месяц*"+"\n" +
                "\n" +
                escapeMarkdown("\uD83C\uDFE6")+"Проведите оплату потратив несколько секунд\n" +
               escapeMarkdown("\uD83D\uDD34")+"Либо отмените подписку в один клик\n" +
                "\n" +
                "> "+escapeMarkdown("❕")+"ВНИМАНИЕ\n" +
                "> "+" При отмене подписки все данные и настройки будут удалены");

        String link ="https://yaroslavnovak.gumroad.com/l/goixa"+"?user_id="+botus.get().getOwner().getId()+"&bot_id="+botus.get().getId();

        InlineKeyboardButton payButton = new InlineKeyboardButton();
        payButton.setText("💳 Оплатить");
        payButton.setUrl(link); // при натисканні відкриється сайт Gumroad

        InlineKeyboardButton denyButton= new InlineKeyboardButton();
        denyButton.setText("\uD83D\uDED1 Отписаться");
        denyButton.setCallbackData("PayDeny:"+botId);

        rows.add(List.of(payButton));
        rows.add(List.of(denyButton));

        markup.setKeyboard(rows);
        sendMessage.setReplyMarkup(markup);

        try {

            Message msgg = bot.execute(sendMessage);
            messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
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
