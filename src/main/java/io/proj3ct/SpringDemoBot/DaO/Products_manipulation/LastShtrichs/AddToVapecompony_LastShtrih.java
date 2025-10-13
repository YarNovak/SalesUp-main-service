package io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs;

import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.DaO.MenuManaging.Deleting.DeleteMedia;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.model.Vapecompony;
import io.proj3ct.SpringDemoBot.model.VapecomponyKatalogRepository;
import io.proj3ct.SpringDemoBot.model.VapecomponyRepository;
import io.proj3ct.SpringDemoBot.model.Vapecompony_katalog;
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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class AddToVapecompony_LastShtrih implements CallbackHandler {

    @Autowired
    private VapecomponyKatalogRepository vapecomponyKatalogRepository;

    @Autowired
    private VapecomponyRepository vapecomponyRepository;

    @Autowired
    private MessageRegistry messageRegistry;

    @Override
    public boolean support(String callbackData) {
        return callbackData.startsWith("ADD_HERE:");
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {

        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);

        String[] parts = query.getData().split(":");

        Long v_kid = Long.parseLong(parts[1]);
        Long vapecompony_id = Long.parseLong(parts[2]);
        Long bot_id = Long.parseLong(parts[3]);

        DeleteMessage dl = new DeleteMessage();
        dl.setMessageId(query.getMessage().getMessageId());
        dl.setChatId(String.valueOf(query.getMessage().getChatId()));

        boolean f = true;

        Vapecompony_katalog vapecompony_katalog = vapecomponyKatalogRepository.findByIdAndBot_Id(v_kid, bot_id).orElse(null);

        if(vapecompony_katalog != null) {

            if(vapecompony_katalog.getVapecompony() == null) f = false;

            vapecompony_katalog.setVapecompony(vapecomponyRepository.findById(vapecompony_id).orElse(null));
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(query.getMessage().getChatId().toString());


            if(vapecompony_katalog.getVapecompony() == null) {

                sendMessage.setText("Не існує більше цієї підменюшки(\nВибери іншу або ж продукт буде неактивним ");

                InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();

                List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

                List<Vapecompony> vcr = StreamSupport.stream(vapecomponyRepository.findAllByBot_Id(Long.valueOf(bot_id)).spliterator(), false)
                        .collect(Collectors.toList());

                while(vcr.size()>0){


                    List<InlineKeyboardButton> rowInLine = new ArrayList<>();

                    var yesButton = new InlineKeyboardButton();

                    vcr.get(0).getName();
                    yesButton.setText(vcr.get(0).getName());
                    yesButton.setCallbackData("ADD_HERE:"+vapecompony_katalog.getId()+":"+vcr.get(0).getId()+":"+bot_id);
                    vcr.remove(0);
                    rowInLine.add(yesButton);

                    rowsInLine.add(rowInLine);

                }


                markupInLine.setKeyboard(rowsInLine);
                sendMessage.setReplyMarkup(markupInLine);

            }
            else if(!f) {

                vapecompony_katalog.setVapecompony(vapecomponyRepository.findById(vapecompony_id).orElse(null));
                vapecomponyKatalogRepository.save(vapecompony_katalog);
/*
                sendMessage.setText("Все вийшло, хочеш додати медіа?");
                List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();


                keyboard.add(List.of(button("🔘 Додати Медіа", "SET_FORPRODMEDIA_YES:" +v_kid+ ":" + bot_id)));
                keyboard.add(List.of(button("📝 Без медіа)", "SET_FORPRODMEDIA_NO:"+ v_kid + ":" + bot_id)));

                InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboard);
                sendMessage.setReplyMarkup(markup);

 */


                String text = (escapeMarkdown("⚙\uFE0F")+" Теперь измените продукт\n" +
                        "\n" +
                        escapeMarkdown("✏\uFE0F")+" _Выберите опцию ниже_"+ escapeMarkdown("\uD83D\uDC47"));

                List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
                keyboard.add(List.of(button("✏\uFE0F Название", "UPDATE_FORPROD_NAME:" + v_kid + ":" + bot_id)));
                keyboard.add(List.of(button("\uD83E\uDD11 Цена", "UPDATE_FORPROD_COST:" + v_kid + ":" + bot_id)));
                keyboard.add(List.of(button("\uD83D\uDCCA Количество", "UPDATE_FORPROD_COUNT:" + v_kid + ":" + bot_id)));
                keyboard.add(List.of(button("\uD83D\uDECD\uFE0F Раздел", "UPDATE_FORPROD_PODMENU:" + v_kid + ":" + bot_id)));
                keyboard.add(List.of(button("📝 Описание", "UPDATE_FORPROD_DESCRIPTION:"+ v_kid + ":" + bot_id)));
                keyboard.add(List.of(button("\uD83C\uDFAC Медиа", "SET_FORPRODMEDIA_YES:"+ v_kid + ":" + bot_id)));
                keyboard.add(List.of(button("❌ УДАЛИТЬ", "DELETE_PRODUCT:"+ v_kid + ":" + bot_id)));
                keyboard.add(List.of(button("\uD83D\uDD19 Назад ", "PRODUCTS_OBSERVE:"+ bot_id)));


                InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboard);


                sendMessage .setChatId(query.getMessage().getChatId().toString());
                sendMessage.setText(text);
                sendMessage.setParseMode("MarkDownV2");
                sendMessage.setReplyMarkup(markup);

            }
            else {

                dl.setChatId(String.valueOf(query.getMessage().getChatId()));

                vapecompony_katalog.setVapecompony(vapecomponyRepository.findById(vapecompony_id).orElse(null));
                vapecomponyKatalogRepository.save(vapecompony_katalog);
                sendMessage.setParseMode("HTML");

                String text = (escapeMarkdown("⚙\uFE0F")+" Теперь измените продукт\n" +
                        "\n" +
                        escapeMarkdown("✏\uFE0F")+" _Выберите опцию ниже_"+ escapeMarkdown("\uD83D\uDC47"));

                EditMessageMedia editMessageMedia = new EditMessageMedia();
                editMessageMedia.setChatId(query.getMessage().getChatId().toString());
                editMessageMedia.setMessageId(query.getMessage().getMessageId());

                InputMediaPhoto inputMediaPhoto = new InputMediaPhoto("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/ProductPTV.png");
                inputMediaPhoto.setParseMode("MarkDownV2");
                inputMediaPhoto.setCaption(text);

                List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
                keyboard.add(List.of(button("✏\uFE0F Название", "UPDATE_FORPROD_NAME:" + v_kid + ":" + bot_id)));
                keyboard.add(List.of(button("\uD83E\uDD11 Цена", "UPDATE_FORPROD_COST:" + v_kid + ":" + bot_id)));
                keyboard.add(List.of(button("\uD83D\uDCCA Количество", "UPDATE_FORPROD_COUNT:" + v_kid + ":" + bot_id)));
                keyboard.add(List.of(button("\uD83D\uDECD\uFE0F Раздел", "UPDATE_FORPROD_PODMENU:" + v_kid + ":" + bot_id)));
                keyboard.add(List.of(button("📝 Описание", "UPDATE_FORPROD_DESCRIPTION:"+ v_kid + ":" + bot_id)));
                keyboard.add(List.of(button("\uD83C\uDFAC Медиа", "SET_FORPRODMEDIA_YES:"+ v_kid + ":" + bot_id)));
                keyboard.add(List.of(button("❌ УДАЛИТЬ", "DELETE_PRODUCT:"+ v_kid + ":" + bot_id)));
                keyboard.add(List.of(button("\uD83D\uDD19 Назад ", "PRODUCTS_OBSERVE:"+ bot_id)));




                InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboard);
                editMessageMedia.setMedia(inputMediaPhoto);
                editMessageMedia.setReplyMarkup(markup);

                try {
                    bot.execute(editMessageMedia);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                sendMessage.setText("✅ Ваш продукт перемещён!\n" +
                        "\n" +
                        "<i>Теперь он находится в новом разделе. Вы можете найти его там и продолжить работу с ним.</i>");


            }

            try {
             //   bot.execute(sendMessage);

                Message msgg =  bot.execute(sendMessage);

                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
                   // bot.execute(dl);


            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

        }
        else {

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(query.getMessage().getChatId().toString());

            sendMessage.setText("Что-то пошло не так, раздела не существует(");

            try {
             //   bot.execute(sendMessage);
                Message msgg =  bot.execute(sendMessage);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
                bot.execute(dl);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

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
