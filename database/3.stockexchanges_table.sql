-- Table: investing.stock_exchanges

-- DROP TABLE investing.stock_exchanges;

CREATE TABLE investing.stock_exchanges
(
    id smallint NOT NULL,
    name character varying(20) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT "StockExchanges_pkey" PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE investing.stock_exchanges
    OWNER to postgres;
