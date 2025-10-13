package io.proj3ct.SpringDemoBot.DB_entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "referrals", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"referrer_id", "referred_user_id"})
})
@Setter
@Getter

public class Referral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "referrer_id")
    private PlatformUser referrer;

    @ManyToOne
    @JoinColumn(name = "referred_user_id")
    private PlatformUser referredUser;

    private Boolean activated = false;

    @Column(name = "activated_date")
    private LocalDateTime activatedDate;

}
