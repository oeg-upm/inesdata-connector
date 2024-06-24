# SQL Federated Catalog

Provides SQL persistence for federated catalog.

## Prerequisites

Please apply this [schema](docs/schema.sql) to your SQL database.

## Entity Diagram

![ER Diagram](////www.plantuml.com/plantuml/png/ZPF1QiCm38RlUGgTn_82ePIU1cMC3ShEnR6Lef5OHbRBHhjtNzA4uR3Cv2hfp_T9mRkeHlJSjGLw9Vq2TFPeZPgMJt0j01w0N0LHXVm9Dfktv-tsNWrzZ2m5utN_Ep1sX0FsJOmLl9YmETnRZ_1QVw3LCOsVWGJxNCtSel5ziIoBxoArSBr5HCrQSDEWP3XhNAPjzodWQGGiEXqZoeKiPiKKfOkpiw1tWSdhkxH9_I-1XbvzLc8_4HgMpkZiOuF7OTHOyXu78kggfQO3B2sNkKrE8k4a0BZTofAlwS-jmB9NGpuk3oxBpLEpLXfDJrb14BwGmrWa-F-dyu1rRZlqRdXPFm00)
<!--
```plantuml
@startuml
entity edc_catalog {
  * id: string <<PK>>
  * participantId: string
  * properties: Map<String, Object>
  * expired: boolean
  --
}

entity edc_dataset {
  * id: string <<PK>>
  * offers: Map<String, Object>
  * properties: Map<String, Object>
  * catalogId: string <<FK>>
  --
}

entity edc_data_service {
  * id: string <<PK>>
  * terms: string
  * endpointUrl: string
  --
}

entity edc_distribution {
  * format: string
  * dataServiceId: string <<FK>>
  * datasetId: string <<FK>>
  --
}

entity edc_catalog_data_service {
  * catalogId: string <<FK>>
  * dataServiceId: string <<FK>>
  --
}

edc_catalog ||--o{ edc_dataset: contains
edc_catalog ||--o{ edc_catalog_data_service: contains
edc_data_service ||--o{ edc_distribution: provides
edc_dataset ||--o{ edc_distribution: contains
edc_data_service ||--o{ edc_catalog_data_service: contains
@enduml
```
-->

## Configuration

| Key                                  | Description                                | Mandatory | 
|:-------------------------------------|:-------------------------------------------|---|
| edc.datasource.federatedCatalog.name | Datasource used to store federated catalog | X |
