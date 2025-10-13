package io.proj3ct.SpringDemoBot.DB_entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "discount_progress", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "bot_id"})
})
@Setter
@Getter
public class DiscountProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private PlatformUser user;

    @ManyToOne
    @JoinColumn(name = "bot_id")
    private Bot bot;

    @Column(name = "discount_applied")
    private BigDecimal discountApplied = BigDecimal.ZERO;

    @Column(name = "is_active")
    private Boolean isActive = true;

}
