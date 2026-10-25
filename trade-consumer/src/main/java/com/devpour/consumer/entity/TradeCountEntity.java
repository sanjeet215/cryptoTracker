package com.devpour.consumer.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "trade_count", uniqueConstraints = @UniqueConstraint(name = "uk_trade_count_symbol", columnNames = "symbol"))
public class TradeCountEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private UUID id;

    @Column(name = "symbol", nullable = false,length = 20)
    private String symbol;

    @Column(name = "trade_count", nullable = false)
    private long tradeCount;

    protected TradeCountEntity() {
    }

    public TradeCountEntity(String symbol, long tradeCount) {
        this.symbol = symbol;
        this.tradeCount = tradeCount;
    }

    public UUID getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public long getTradeCount() {
        return tradeCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TradeCountEntity other)) return false;
        return Objects.equals(symbol, other.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(symbol);
    }

    public void increment(long delta) {
        this.tradeCount += delta;
    }
}
