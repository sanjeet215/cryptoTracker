package com.devpour.consumer.entity;

import com.devpour.consumer.domain.TradeEvent;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "trade")
public class TradeEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private UUID id;

    @Embedded
    private TradeId tradeId;

    @Column(nullable = false, precision = 30, scale = 10)
    private BigDecimal price;

    @Column(nullable = false, precision = 30, scale = 10)
    private BigDecimal quantity;

    @Column(name = "trade_time", nullable = false)
    private Instant tradeTime;

    @Column(name = "buyer_maker", nullable = false)
    private boolean buyerMaker;

    @Column(name = "ingested_at", nullable = false, insertable = false, updatable = false)
    private Instant ingestedAt;   // filled by the DB default

    protected TradeEntity() {
    }    // JPA needs a no-arg constructor; protected keeps it out of your code

    public static TradeEntity from(TradeEvent e) {
        TradeEntity t = new TradeEntity();
        t.tradeId = new TradeId(e.symbol(), e.tradeId());
        t.price = e.price();
        t.quantity = e.quantity();
        t.tradeTime = e.tradeTime();
        t.buyerMaker = e.buyerMaker();
        return t;
    }


    // getters only, no setters: a trade never changes once stored

    public UUID getId() {
        return id;
    }

    public TradeId getTradeId() {
        return tradeId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public Instant getTradeTime() {
        return tradeTime;
    }

    public boolean isBuyerMaker() {
        return buyerMaker;
    }

    public Instant getIngestedAt() {
        return ingestedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TradeEntity other)) return false;
        return Objects.equals(tradeId, other.tradeId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tradeId);
    }
}
