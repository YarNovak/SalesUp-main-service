package io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class MessageRegistry {

    @Setter
    @Getter
    private Map<Long, Integer> up_to_salesUp = new ConcurrentHashMap<>();
    private final Map<Long, Deque<Integer>> messagesToDelete = new ConcurrentHashMap<>();
    @Setter
    @Getter
    private StartMessage startMessage;

    public void addMessage(Long chatId, Integer messageId) {
        messagesToDelete
                .computeIfAbsent(chatId, k -> new ConcurrentLinkedDeque<>())
                .addLast(messageId);
    }



    public void clearMessages(Long chatId) {
        messagesToDelete.remove(chatId);
    }


    public void deleteMessagesBefore(Long chatId, Integer targetMessageId, boolean inclusive, AbsSender bot) {
        Deque<Integer> deque = messagesToDelete.get(chatId);
        if (deque == null || deque.isEmpty()) return;

        // идём с начала очереди (самые старые сообщения)
        Iterator<Integer> it = deque.iterator();
        while (it.hasNext()) {
            Integer messageId = it.next();

            boolean shouldDelete = inclusive
                    ? messageId <= targetMessageId
                    : messageId < targetMessageId;

            if (shouldDelete) {
                try {
                    bot.execute(new DeleteMessage(chatId.toString(), messageId));
                    System.out.println("✅ Видалено повідомлення: " + messageId);
                } catch (Exception e) {
                    System.err.println("❌ Не вдалося видалити повідомлення " + messageId + ": " + e.getMessage());
                }
                it.remove();
            } else {
                // дальше идут более новые сообщения — их не трогаем
                break;
            }
        }

        if (deque.isEmpty()) {
            messagesToDelete.remove(chatId);
        }
    }

    public void deleteMessagesAfter(Long chatId, Integer targetMessageId, boolean inclusive, AbsSender bot) {

        Deque<Integer> deque = messagesToDelete.get(chatId);
        if (deque == null || deque.isEmpty()) return;

        for (Integer messageId : List.copyOf(deque)) {
            boolean shouldDelete = inclusive
                    ? messageId >= targetMessageId
                    : messageId > targetMessageId;

            if (shouldDelete) {
                try {
                    bot.execute(new DeleteMessage(chatId.toString(), messageId));
                    System.out.println("✅ Видалено повідомлення: " + messageId);
                } catch (Exception e) {
                    System.err.println("❌ Не вдалося видалити повідомлення " + messageId + ": " + e.getMessage());
                } finally {
                    deque.remove(messageId);
                }
            }
        }

        if (deque.isEmpty()) {
            messagesToDelete.remove(chatId);
        }


/*
        Deque<Integer> deque = messagesToDelete.get(chatId);
        if (deque == null || deque.isEmpty()) {
           return;}

        Iterator<Integer> it = deque.descendingIterator();

        while (it.hasNext()) {
            Integer messageId = it.next();

            // якщо дійшли до цільового
            if (messageId.equals(targetMessageId)) {
                if (inclusive) {
                    try {
                        bot.execute(new DeleteMessage(chatId.toString(), messageId));
                        System.out.println("✅ Видалено повідомлення: " + messageId);
                    } catch (Exception e) {
                        System.err.println("❌ Не вдалося видалити повідомлення " + messageId + ": " + e.getMessage());
                    }
                    it.remove();
                }
                break; // далі вже не видаляємо
            }

            // усі інші (після target) видаляємо
            try {
                bot.execute(new DeleteMessage(chatId.toString(), messageId));
                System.out.println("✅ Видалено повідомлення: " + messageId);
            } catch (Exception e) {
                System.err.println("❌ Не вдалося видалити повідомлення " + messageId + ": " + e.getMessage());
            }
            it.remove();
        }

        if (deque.isEmpty()) {
            messagesToDelete.remove(chatId);
        }
 */

    }


    private final Map<Long, StartMessage> startMessages = new ConcurrentHashMap<>();

    public void registerStartMessage(Long chatId, Integer messageId) {
        startMessages.compute(chatId, (id, old) -> {
            if (old == null) {
                return new StartMessage(chatId, messageId);
            } else {
                old.setMessageId(messageId); // оновлюємо messageId
                return old;
            }
        });
    }

    public StartMessage getStartMessage(Long chatId) {
        return startMessages.get(chatId);
    }

    @Setter
    @Getter
    public  static class StartMessage{

        Long chatId;
        Integer messageId;
        public StartMessage(Long chatId, Integer messageId) {
            this.chatId = chatId;
            this.messageId = messageId;
        }
    }

}
