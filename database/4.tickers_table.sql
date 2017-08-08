-- Table: investing.tickers

-- DROP TABLE investing.tickers;

CREATE TABLE investing.tickers
(
    id integer NOT NULL DEFAULT nextval('investing."Tickers_id_seq"'::regclass),
    name text COLLATE pg_catalog."default" NOT NULL,
    sector text COLLATE pg_catalog."default" NOT NULL,
    industry text COLLATE pg_catalog."default",
    created_date timestamp with time zone NOT NULL,
    stock_exchange smallint NOT NULL,
    symbol character varying(20) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT "Tickers_pkey" PRIMARY KEY (id),
    CONSTRAINT stock_exchange_fk FOREIGN KEY (stock_exchange)
        REFERENCES investing.stock_exchanges (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE investing.tickers
    OWNER to postgres;
    