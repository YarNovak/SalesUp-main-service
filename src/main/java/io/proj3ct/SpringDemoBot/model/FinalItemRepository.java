package io.proj3ct.SpringDemoBot.model;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FinalItemRepository extends CrudRepository<FinalItem, Long> {

    // Автоматична фільтрація по bot_id для всіх замовлень
    //@Query("SELECT o FROM FinalItem o WHERE o.bot.id = :botId")
    //List<FinalItem> findAllByBotId(@Param("botId") Long botId);

    List<FinalItem> findAllByBot_Id(Long botId);

  //  @Query("SELECT f FROM FinalItem f WHERE f.bot.id = :botId")
   // List<FinalItem> findAllByBot_Id(@Param("botid") Long botId);


    public void deleteAllByBot(Bot bot);

}
