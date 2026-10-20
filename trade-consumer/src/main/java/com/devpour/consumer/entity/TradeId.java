package com.devpour.consumer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public record TradeId(@Column(name = "symbol", nullable = false, length = 20) String symbol,
                      @Column(name = "trade_id", nullable = false) long tradeId) implements Serializable
{}
