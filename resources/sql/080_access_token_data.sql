CREATE TABLE IF NOT EXISTS edc_accesstokendata
(
    id           VARCHAR NOT NULL PRIMARY KEY,
    claim_token  JSON    NOT NULL,
    data_address JSON    NOT NULL,
    additional_properties JSON DEFAULT '{}'
);