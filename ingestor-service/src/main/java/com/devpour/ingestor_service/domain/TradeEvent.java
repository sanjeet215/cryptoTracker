package com.devpour.ingestor_service.domain;

import java.math.BigDecimal;
import java.time.Instant;

public final record TradeEvent(
        String symbol,
        long tradeId,
        BigDecimal price,
        BigDecimal quantity,
        Instant tradeTime,
        boolean buyerMaker
) {
}
