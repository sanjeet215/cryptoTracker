package com.devpour.ingestor_service.binance;

import com.devpour.ingestor_service.config.BinanceProperties;
import com.devpour.ingestor_service.domain.TradeEvent;
import com.devpour.ingestor_service.kafka.TradePublisher;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Instant;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Component
public class BinanceTradeStreamClient implements WebSocket.Listener {

    private static final Logger log = LoggerFactory.getLogger(BinanceTradeStreamClient.class);

    private final BinanceProperties props;
    private final JsonMapper jsonMapper;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final TradePublisher tradePublisher;

    // A big message can arrive in parts; collect until last == true
    private final StringBuilder buffer = new StringBuilder();
    private volatile WebSocket webSocket;

    public BinanceTradeStreamClient(BinanceProperties props, JsonMapper jsonMapper,TradePublisher tradePublisher) {
        this.props = props;
        this.jsonMapper = jsonMapper;
        this.tradePublisher = tradePublisher;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void connect() {
        URI uri = buildUri();
        log.info("Connecting to {}", uri);
        httpClient.newWebSocketBuilder()
                .buildAsync(uri, this)
                .whenComplete((ws, ex) -> {
                    if (ex != null) log.error("Connection failed", ex);
                });
    }

    private URI buildUri() {
        String streams = props.symbols().stream()
                .map(s -> s.toLowerCase() + "@trade")
                .collect(Collectors.joining("/"));
        return URI.create(props.wsBaseUrl() + "?streams=" + streams);
    }

    @Override
    public void onOpen(WebSocket ws) {
        this.webSocket = ws;
        log.info("Connected. Subscribed to {} symbols", props.symbols().size());
        ws.request(1);   // backpressure: ask for the first message
    }

    @Override
    public CompletionStage<?> onText(WebSocket ws, CharSequence data, boolean last) {
        buffer.append(data);
        if (last) {
            handle(buffer.toString());
            buffer.setLength(0);
        }
        ws.request(1);   // ask for the next message, or the stream stops
        return null;
    }

    private void handle(String json) {
        try {
            BinanceEnvelope env = jsonMapper.readValue(json, BinanceEnvelope.class);
            TradeEvent trade = toTradeEvent(env.data());
            log.info("{} {} x {}", trade.symbol(), trade.price(), trade.quantity());
            tradePublisher.publish(trade);
        } catch (Exception e) {
            log.warn("Could not parse message: {}", json, e);
        }
    }

    private TradeEvent toTradeEvent(BinanceEnvelope.Trade t) {
        return new TradeEvent(t.symbol(), t.tradeId(), t.price(), t.quantity(),
                Instant.ofEpochMilli(t.tradeTime()), t.buyerMaker());
    }

    @Override
    public CompletionStage<?> onClose(WebSocket ws, int statusCode, String reason) {
        log.warn("Closed: {} {}", statusCode, reason);   // 2c: reconnect here
        return null;
    }

    @Override
    public void onError(WebSocket ws, Throwable error) {
        log.error("WebSocket error", error);             // 2c: reconnect here
    }

    // Close cleanly when the app stops
    @PreDestroy
    public void shutdown() {
        if (webSocket != null) {
            webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "shutdown");
        }
    }
}
