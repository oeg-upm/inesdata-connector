-- table: edc_vocabulary
CREATE TABLE IF NOT EXISTS edc_vocabulary
(
    id                 VARCHAR NOT NULL,
    created_at         BIGINT  NOT NULL,
    json_schema        JSON    DEFAULT '{}',
    name               VARCHAR NOT NULL,
    connector_id       VARCHAR NOT NULL,
    category           VARCHAR NOT NULL,
    PRIMARY KEY (id, connector_id)
);

COMMENT ON COLUMN edc_vocabulary.json_schema IS 'JSON Schema with the vocabulary';