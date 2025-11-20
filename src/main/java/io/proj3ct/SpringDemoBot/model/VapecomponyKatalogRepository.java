package io.proj3ct.SpringDemoBot.model;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface VapecomponyKatalogRepository extends CrudRepository<Vapecompony_katalog, Long> {

    Optional<Vapecompony_katalog> findVapecompony_katalogByNameAndBot_Id(String name, Long id);
    //Optional<Vapecompony_katalog> findByVapecompony_katalog(Vapecompony_katalog vapecompony_katalog);
   // List<CartItem> findByChatId(Long chatId);
   // Optional<CartItem> findByChatIdAndVapecomponyKatalog_Id(Long chatId, Long productId);
  //  List<Vapecompony_katalog> findById(Long id);
   // List<Vapecompony_katalog> findByNameAndBot_Id(String name, Long bot_id);

    Optional<Vapecompony_katalog> findByNameAndBot_Id(String name, Long id);

    List<Vapecompony_katalog> findAllByBot_Id(Long id);

    @EntityGraph(attributePaths = {"vapecompony"})
    List<Vapecompony_katalog> findAllByBot_BotToken(String token);


   // Optional<Vapecompony_katalog> findVapecompony_katalogById(Long id);
    List<Vapecompony_katalog> findByVapecompony_idAndBot_Id(Long id, Long bot_id);
    List<Vapecompony_katalog> findByVapecompony(Vapecompony vapecompony);
    Optional<Vapecompony_katalog> findByIdAndBot_Id(Long id, Long bot_id);

    public void deleteAllByBot(Bot bot);
    public void deleteAllByBot_Id(Long botId);

  @Modifying
  @Transactional
  @Query("DELETE FROM Vapecompony_katalog v WHERE v.bot.id = :botId")
  void deleteBulkByBotId(@Param("botId") Long botId);

 //   @Query("SELECT o FROM vapecompony_katalog o WHERE o.bot.id = :botId")
  //  List<Vapecompony_katalog> findAllByBotId(@Param("botId") Long botId);

}
