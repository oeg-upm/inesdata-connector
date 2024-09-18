-- Tabla para Catalog
CREATE TABLE  IF NOT EXISTS edc_catalog (
    id              VARCHAR NOT NULL,
    participant_id  VARCHAR(255) NOT NULL,
    properties      JSON DEFAULT '{}',
    expired         BOOLEAN,
    PRIMARY KEY (id),
    UNIQUE (participant_id)
);

-- Tabla para Dataset
CREATE TABLE IF NOT EXISTS edc_dataset (
    id              VARCHAR NOT NULL,
    offers          JSON DEFAULT '{}',
    properties      JSON DEFAULT '{}',
    catalog_id VARCHAR,
    FOREIGN KEY (catalog_id) REFERENCES edc_catalog(id),
    PRIMARY KEY (id, catalog_id)
);

-- Tabla para DataService
CREATE TABLE IF NOT EXISTS edc_data_service (
    id              VARCHAR NOT NULL,
    terms VARCHAR(255),
    endpoint_url VARCHAR(255),
    PRIMARY KEY (id)
);

-- Tabla para Distribution
CREATE TABLE IF NOT EXISTS edc_distribution (
    format VARCHAR(255),
    data_service_id VARCHAR,
    dataset_id VARCHAR,
    catalog_id VARCHAR,
    FOREIGN KEY (data_service_id) REFERENCES edc_data_service(id),
    FOREIGN KEY (dataset_id, catalog_id) REFERENCES edc_dataset(id, catalog_id)
);

-- Tabla de relación entre Catalog y DataService (para la lista de dataServices en Catalog)
CREATE TABLE IF NOT EXISTS edc_catalog_data_service (
    catalog_id VARCHAR,
    data_service_id VARCHAR,
    PRIMARY KEY (catalog_id, data_service_id),
    FOREIGN KEY (catalog_id) REFERENCES edc_catalog(id),
    FOREIGN KEY (data_service_id) REFERENCES edc_data_service(id)
);