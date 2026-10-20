package com.devpour.consumer.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record TradeEvent(String symbol, long tradeId, BigDecimal price,
                         BigDecimal quantity, Instant tradeTime, boolean buyerMaker) {}