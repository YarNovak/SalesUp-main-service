package io.proj3ct.SpringDemoBot.model;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    //List<User> findBylastUpdated(LocalDateTime time);

    Optional<User> findByChatIdAndBot_Id(Long chatId, Long botId);
    List<User> findAllByBot_Id(Long botId);
    List<User> findByLastUpdatedBeforeAndBot_Id(Timestamp lastUpdated, Long botId);


    List<User> findAllByBot_BotToken(String botToken);

    boolean existsByChatIdAndBot_Id(Long chatId, Long botId);

  void deleteAllByBot_Id(Long botId);

  @Modifying
  @Transactional
  @Query("DELETE FROM User u WHERE u.bot.id = :botId")
  void deleteBulkByBotId(@Param("botId") Long botId);

    // Автоматична фільтрація по bot_id для всіх замовлень
 //   @Query("SELECT o FROM users o WHERE o.bot.id = :botId")
  //  List<User> findAllByBotId(@Param("botId") Long botId);



}
