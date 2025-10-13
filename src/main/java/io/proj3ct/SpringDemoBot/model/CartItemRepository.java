package io.proj3ct.SpringDemoBot.model;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends CrudRepository<CartItem, Long> {

    List<CartItem> findByChatIdAndBot_Id(Long chatId, Long bot_id);
    List<CartItem>  findByChatIdAndBot_IdOrderById(Long chatId, Long bot_id);
    Optional<CartItem> findByIdAndBot_Id(Long id, Long bot_id);
    Optional<CartItem> findByChatIdAndVapecomponyKatalog_IdAndBot_Id(Long chatId, Long productId, Long bot_id);

    List<CartItem> findAllByVapecomponyKatalog_IdAndBot_Id(Long chatId, Long bot_id);

    List<CartItem> findAllByBot_Id(Long bot_id);

    Optional<CartItem> findByChatIdAndVapecomponyKatalog_NameAndBot_Id(Long chatId, String vapecomponyName, Long bot_id);

    void deleteByChatId(Long chatId);

    public void deleteAllByBot(Bot bot);
    // Автоматична фільтрація по bot_id для всіх замовлень
 //   @Query("SELECT o FROM CartItem o WHERE o.bot.id = :botId")
 //   List<CartItem> findAllByBotId(@Param("botId") Long botId);

}
