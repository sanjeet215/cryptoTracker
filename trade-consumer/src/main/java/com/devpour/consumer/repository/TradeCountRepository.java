package com.devpour.consumer.repository;

import com.devpour.consumer.entity.TradeCountEntity;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface TradeCountRepository extends JpaRepository<TradeCountEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)              // SELECT ... FOR UPDATE
    List<TradeCountEntity> findBySymbolInOrderBySymbol(Collection<String> symbols);
}
