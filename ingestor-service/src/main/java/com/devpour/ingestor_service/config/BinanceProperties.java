package com.devpour.ingestor_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.List;

@ConfigurationProperties(prefix = "binance")
public record BinanceProperties(String wsBaseUrl, List<String> symbols) {}