package io.proj3ct.SpringDemoBot.dopclasses;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.media.*;

@Component
public class EditMessage_Service {



    public void editEverything(
            TelegramLongPollingBot bot,
            Long chatId,
            Integer messageId,
            String newText,             // новий текст/підпис (може бути null)
            String newMediaUrl,         // нове медіа (null = не змінювати медіа)
            String mediaType,           // "photo", "video", "document", "animation", "audio", або null якщо текстове
            InlineKeyboardMarkup newMarkup // нова клавіатура (може бути null)
    ) {
        try {
            // Випадок 1: є медіа → використовуємо EditMessageMedia
            if (newMediaUrl != null && mediaType != null) {
                EditMessageMedia editMedia = new EditMessageMedia();
                editMedia.setChatId(chatId.toString());
                editMedia.setMessageId(messageId);

                InputMedia media;
                switch (mediaType.toLowerCase()) {
                    case "photo":
                        media = new InputMediaPhoto(newMediaUrl);
                        break;
                    case "video":
                        media = new InputMediaVideo(newMediaUrl);
                        break;
                    case "document":
                        media = new InputMediaDocument(newMediaUrl);
                        break;
                    case "animation":
                        media = new InputMediaAnimation(newMediaUrl);
                        break;
                    case "audio":
                        media = new InputMediaAudio(newMediaUrl);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown mediaType: " + mediaType);
                }

                if (newText != null) {
                    media.setCaption(newText);
                }
                editMedia.setMedia(media);

                if (newMarkup != null) {
                    editMedia.setReplyMarkup(newMarkup);
                }

                bot.execute(editMedia);
                return;
            }

            // Випадок 2: немає медіа → текстове повідомлення
            if (newText != null || newMarkup != null) {
                EditMessageText editText = new EditMessageText();
                editText.setChatId(chatId.toString());
                editText.setMessageId(messageId);

                if (newText != null) {
                    editText.setText(newText);
                }
                if (newMarkup != null) {
                    editText.setReplyMarkup(newMarkup);
                }

                bot.execute(editText);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
