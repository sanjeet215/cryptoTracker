CREATE TABLE trade_count (
                             id           UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                             symbol       VARCHAR(20) NOT NULL,
                             trade_count  BIGINT      NOT NULL DEFAULT 0,
                             CONSTRAINT uk_trade_count_symbol UNIQUE (symbol)
);