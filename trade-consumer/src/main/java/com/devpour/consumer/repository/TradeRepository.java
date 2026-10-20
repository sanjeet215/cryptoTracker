package com.devpour.consumer.repository;

import com.devpour.consumer.entity.TradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TradeRepository extends JpaRepository<TradeEntity, UUID> {
}
