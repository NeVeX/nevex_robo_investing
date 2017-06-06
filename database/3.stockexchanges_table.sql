

CREATE TABLE investing."StockExchanges"
(
    "Id" smallint NOT NULL,
    "Name" character varying(20) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT "StockExchanges_pkey" PRIMARY KEY ("Id")
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE investing."StockExchanges"
    OWNER to postgres;


