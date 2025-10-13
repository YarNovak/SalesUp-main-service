package io.proj3ct.SpringDemoBot.model;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface OrdersRepository extends CrudRepository<Orders, Long> {



    //Optional<Orders> findByChatId(Long id);
   // Optional<Orders> findByUser_ChatId(long userId);
   // Optional<Orders> findByUser_Id(long userId);
    Optional<Orders> findByUser(User user);
    Optional<Orders> findByUser_ChatId(Long id);
    Optional<Orders> findByUser_ChatIdAndPaidEqualsAndBot_Id(Long id, Boolean paid, Long botI_d);
    List<Orders> findByCreatedAtBefore(Timestamp before);

    Optional<Orders> findByIdAndBot_Id(Long id, Long boti_d);

    @EntityGraph(attributePaths = {"finalItems", "finalItems.bot", "user"})
    List<Orders> findAllByBot_BotToken(String botToken);
    public void deleteAllByBot(Bot bot);

    List<Orders> findAllByBot_Id(Long id);

    // Автоматична фільтрація по bot_id для всіх замовлень
 //   @Query("SELECT o FROM Orders o WHERE o.bot.id = :botId")
//    List<Orders> findAllByBotId(@Param("botId") Long botId);

    // Фільтрація за користувачем + bot_id
 //   @Query("SELECT o FROM Orders o WHERE o.user = :user AND o.bot.id = :botId")
  //  Optional<Orders> findByUserAndBotId(@Param("user") User user, @Param("botId") Long botId);

    // Фільтрація за chatId + bot_id
  //  @Query("SELECT o FROM Orders o WHERE o.user.chatId = :chatId AND o.bot.id = :botId")
  //  Optional<Orders> findByUserChatIdAndBotId(@Param("chatId") Long chatId, @Param("botId") Long botId);

}
