-- 1. Add the column, nullable first (fast)
ALTER TABLE trade ADD COLUMN id UUID;

-- 2. Backfill existing rows (v4 is fine for historic rows)
UPDATE trade SET id = gen_random_uuid() WHERE id IS NULL;

-- 3. Safety net: inserts from old code without an id still work during rollout
ALTER TABLE trade ALTER COLUMN id SET DEFAULT gen_random_uuid();
ALTER TABLE trade ALTER COLUMN id SET NOT NULL;

-- 4. Keep the natural key unique BEFORE dropping the old PK
ALTER TABLE trade ADD CONSTRAINT uk_trade_symbol_trade_id UNIQUE (symbol, trade_id);

-- 5. Swap the primary key
ALTER TABLE trade DROP CONSTRAINT pk_trade;
ALTER TABLE trade ADD CONSTRAINT pk_trade PRIMARY KEY (id);