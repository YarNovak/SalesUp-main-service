package io.proj3ct.SpringDemoBot.DB_entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "platform_users")
@Setter
@Getter
public class PlatformUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "telegram_id", unique = true)
    private Long telegramId;

    private String username;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate = LocalDateTime.now();

    @Column(name = "discount_stage")
    private Integer discountStage = 0;

    @Column(name = "total_referrals")
    private Integer totalReferrals = 0;

    @ManyToOne
    @JoinColumn(name = "referred_by_user_id")
    private PlatformUser referredBy;

    @OneToOne
    @JoinColumn(name = "current_bot_discount_target_id")
    private Bot currentBotDiscountTarget;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bot> bots = new ArrayList<>();

    @OneToMany(mappedBy = "referrer")
    private List<Referral> referralsMade = new ArrayList<>();

    @OneToMany(mappedBy = "referredUser")
    private List<Referral> referralsReceived = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<DiscountProgress> discountProgressList = new ArrayList<>();

}
