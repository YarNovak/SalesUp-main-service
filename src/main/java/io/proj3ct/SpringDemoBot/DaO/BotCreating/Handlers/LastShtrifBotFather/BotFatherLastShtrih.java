package io.proj3ct.SpringDemoBot.DaO.BotCreating.Handlers.LastShtrifBotFather;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DB_entities.PlatformUser;
import io.proj3ct.SpringDemoBot.DaO.BotCreating.BotDefaultValues;
import io.proj3ct.SpringDemoBot.DaO.BotCreating.Handlers.Wait_BotFather;
import io.proj3ct.SpringDemoBot.DaO.BotCreating.TgTokenvalidator;
import io.proj3ct.SpringDemoBot.DaO.MessageHandle;

import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.repository.PlatformUserRepository;
import io.proj3ct.SpringDemoBot.service.BotService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class BotFatherLastShtrih implements MessageHandle {

    @Autowired
    private Wait_BotFather waitBotFather;

    @Autowired
    private PlatformUserRepository userRepository;
    
    @Autowired
    private BotService botService;

    @Autowired
    private MessageRegistry messageRegistry;

    @Override
    public boolean support(Message msgcallbackData) {
        return waitBotFather.iswaiting_foruser(msgcallbackData.getFrom().getId());
    }

    @Override
    public void handle(Message msg, TelegramLongPollingBot bot) {

        SendMessage message = new SendMessage();
        message.setChatId(msg.getChatId().toString());

            if(TgTokenvalidator.isValidTelegramToken(msg.getText())){

                Long userId = msg.getFrom().getId();

                PlatformUser user = userRepository.findByTelegramId(userId)
                        .orElseThrow(() -> new IllegalArgumentException(
                            "User with the provided Telegram ID does not exist."));
                
                Optional<Bot> botikOptional = botService.createBotFreeTrial(user, msg.getText(), true);
                // Если бот не был создан (токен уже используется)
                if (botikOptional.isEmpty()) {
                    message.setText("Ваш токен вже використовується іншим ботом, надішліть інший))");
                    try{
                        Message msgg =  bot.execute(message);
                        messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

                    }
                    catch (TelegramApiException e){
                        e.printStackTrace();
                    }
                    return;
                }
                Bot botik = botikOptional.get();
                waitBotFather.clear(msg.getFrom().getId());

                String botUsername = botService.extractUsernameFromToken(botik.getBotToken());
                String link = "https://t.me/" + botUsername;

                String text =
                        "🎉 Ваш бот успешно создан!\n\n" +
                                "Запустите его тут:\n" + link + "\n\n" +
                                "🔓 Пробный период: 7 дней.";

                InlineKeyboardButton goToBotButton = new InlineKeyboardButton("🚀 Открыть бота");
                goToBotButton.setUrl(link);

                InlineKeyboardButton editFunctionalityButton = new InlineKeyboardButton("⚙ Изменить функционал бота");
                editFunctionalityButton.setCallbackData("EDIT_BOT_FUNCTION:" + botik.getBotToken());

                List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
                keyboard.add(Collections.singletonList(goToBotButton));
                keyboard.add(Collections.singletonList(editFunctionalityButton));

                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                markup.setKeyboard(keyboard);

                message.setText(text);
                message.setReplyMarkup(markup);



            }
            else{

                message.setText("Ваш токен або неправильний,а бо ж не дыйсний, надышлыть новий))");

            }
            try{
                Message msgg =  bot.execute(message);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

            }
            catch (TelegramApiException e){
                e.printStackTrace();
            }

    }
}
