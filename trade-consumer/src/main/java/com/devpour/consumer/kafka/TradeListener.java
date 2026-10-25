package com.devpour.consumer.kafka;

import com.devpour.consumer.domain.TradeEvent;
import com.devpour.consumer.entity.TradeCountEntity;
import com.devpour.consumer.entity.TradeEntity;
import com.devpour.consumer.repository.TradeCountRepository;
import com.devpour.consumer.repository.TradeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TradeListener {

    private static final Logger log = LoggerFactory.getLogger(TradeListener.class);

    private final TradeRepository tradeRepository;

    private final TradeCountRepository tradeCountRepository;

    public TradeListener(TradeRepository tradeRepository, TradeCountRepository tradeCountRepository) {
        this.tradeRepository = tradeRepository;
        this.tradeCountRepository = tradeCountRepository;
    }

//    @KafkaListener(topics = "${app.kafka.trades-topic}")
//    @Transactional(rollbackFor = Exception.class)
//    public void onTrade(@Payload TradeEvent tradeEvent,
//                        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
//        tradeRepository.save(TradeEntity.from(tradeEvent));
//        log.debug("Trade event received: {}, partition: {}", tradeEvent, partition);
//    }

    @KafkaListener(topics = "${app.kafka.trades-topic}", batch = "true")
    @Transactional(rollbackFor = Exception.class)
    public void onTrade(List<TradeEvent> tradeEvents) {
        List<TradeEntity> tradeEntityList = tradeEvents.stream().map(TradeEntity::from).toList();
        tradeRepository.saveAll(tradeEntityList);

        Map<String, Long> countBySymbol = tradeEntityList.stream()
                .collect(Collectors.groupingBy(tradeEntity -> tradeEntity.getTradeId().symbol(), Collectors.counting()));

        Map<String, TradeCountEntity> existing = tradeCountRepository
                .findBySymbolInOrderBySymbol(countBySymbol.keySet()).stream()
                .collect(Collectors.toMap(TradeCountEntity::getSymbol, c -> c));

        countBySymbol.forEach((symbol, delta) -> updateTradeCount(symbol, delta, existing));
    }

    private void updateTradeCount(String symbol, Long delta, Map<String, TradeCountEntity> existing) {
        TradeCountEntity tradeCountEntity = existing.get(symbol);
        log.debug("updateTradeCount: delta: {}",delta );
        if (tradeCountEntity != null)
        {
            tradeCountEntity.increment(delta);
        } else
        {
            tradeCountRepository.save(new TradeCountEntity(symbol, delta));
        }
    }
}
