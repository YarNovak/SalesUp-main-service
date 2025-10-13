package io.proj3ct.SpringDemoBot.DB_entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "bot_message")
@Setter
@Getter
public class BotMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String messageKey;

    @Lob
    @Column(nullable = true)
    private byte[] photo;

    private String photoMimeType;

    @Lob
    @Column(nullable = true)
    private byte[] video;

    private String videoMimeType;

    private Timestamp photo_updatedAt;
    private Timestamp video_updatedAt;

    @Column(columnDefinition = "TEXT")
    private String text;

    @Lob
    @Column(name = "entities_json", columnDefinition = "TEXT")
    private String entitiesJson;

    @Column(columnDefinition = "TEXT")
    private String default_text;

    @Lob
    @Column(name = "entities_default_json", columnDefinition = "TEXT")
    private String default_entitiesJson;

    // Constructors
    public BotMessage() {}

    public BotMessage(String messageKey, byte[] photo, String photoMimeType,
                      byte[] video, String videoMimeType, Timestamp photo_updatedAt,  Timestamp video_updatedAt, Bot bot) {
        this.messageKey = messageKey;
        this.photo = photo;
        this.photoMimeType = photoMimeType;
        this.video = video;
        this.videoMimeType = videoMimeType;
        this.photo_updatedAt = photo_updatedAt;
        this.video_updatedAt = video_updatedAt;
        this.bot = bot;
    }

    public BotMessage(String messageKey, String text, Bot bot) {
        this.messageKey = messageKey;
        this.text = text;
        this.bot = bot;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bot_id", nullable = false)
    private Bot bot;



    // Getters & Setters ...
}
