package com.devpour.ingestor_service.kafka;

import com.devpour.ingestor_service.domain.TradeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
public class TradePublisher {
    private static final Logger log = LoggerFactory.getLogger(TradePublisher.class);

    private final KafkaTemplate<String, TradeEvent> kafkaTemplate;
    private final String topic;


    public TradePublisher(KafkaTemplate<String, TradeEvent> kafkaTemplate,
                          @Value("${app.kafka.trades-topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(TradeEvent tradeEvent)
    {
        log.debug("Publishing trade: {}", tradeEvent);
        kafkaTemplate.send(topic,tradeEvent.symbol(),tradeEvent).whenComplete(TradePublisher::doPerformOnEachEvent);
    }

    private static void doPerformOnEachEvent(SendResult<String, TradeEvent> trade, Throwable throwable) {
        if(throwable != null) {
            log.error("Error publishing trade: {}", trade, throwable);
        }
        else if(log.isDebugEnabled())
        {
            var md = trade.getRecordMetadata();
            log.debug("Sent {} -> partition {} offset {}", trade, md.partition(), md.offset());
        }
    }
}
