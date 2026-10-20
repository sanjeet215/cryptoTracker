CREATE TABLE trade (
                       symbol       VARCHAR(20)    NOT NULL,
                       trade_id     BIGINT         NOT NULL,
                       price        NUMERIC(30,10) NOT NULL,
                       quantity     NUMERIC(30,10) NOT NULL,
                       trade_time   TIMESTAMPTZ    NOT NULL,
                       buyer_maker  BOOLEAN        NOT NULL,
                       ingested_at  TIMESTAMPTZ    NOT NULL DEFAULT now(),
                       CONSTRAINT pk_trade PRIMARY KEY (symbol, trade_id)
);

-- Main read pattern: "trades for symbol X in a time range, newest first"
CREATE INDEX idx_trade_symbol_time ON trade (symbol, trade_time DESC);