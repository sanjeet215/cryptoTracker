package com.devpour.ingestor_service.binance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BinanceEnvelope(String stream, Trade data) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Trade(
            @JsonProperty("s") String symbol,
            @JsonProperty("t") long tradeId,
            @JsonProperty("p") BigDecimal price,     // sent as string, Jackson converts
            @JsonProperty("q") BigDecimal quantity,
            @JsonProperty("T") long tradeTime,       // capital T = trade time; small e/E ignored
            @JsonProperty("m") boolean buyerMaker
    ) {}
}