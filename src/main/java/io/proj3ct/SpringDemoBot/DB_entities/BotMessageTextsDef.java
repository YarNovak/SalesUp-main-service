package io.proj3ct.SpringDemoBot.DB_entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "bot_message_texts_def")
@Setter
@Getter
public class BotMessageTextsDef {

    @Id
    private String messageKey;

    @Column(columnDefinition = "TEXT")
    private String text;

    @Lob
    @Column(name = "entities_json", columnDefinition = "TEXT")
    private String entitiesJson;

}
