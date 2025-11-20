package io.proj3ct.SpringDemoBot.model;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface VapecomponyRepository extends CrudRepository<Vapecompony, Long> {

    Optional<Vapecompony> findVapecomponyByName(String name);
    List<Vapecompony> findAllByBot_Id(Long id);
    Optional<Vapecompony> findByIdAndBot_Id(Long idA,Long idB);
    Optional<Vapecompony> findByNameAndBot_Id(String name,Long id);

    public void deleteAllByBot(Bot bot);
    public void deleteAllByBot_Id(Long botId);
  
    @Modifying
    @Transactional
    @Query("DELETE FROM Vapecompony v WHERE v.bot.id = :botId")
    void deleteBulkByBotId(@Param("botId") Long botId);
  //  @Query("SELECT o FROM Vapecompony o WHERE o.bot.id = :botId")
  //  List<Vapecompony> findAllByBotId(@Param("botId") Long botId);
}
