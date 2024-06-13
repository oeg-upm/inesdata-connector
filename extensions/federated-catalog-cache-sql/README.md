# SQL Federated Catalog

Provides SQL persistence for federated catalog.

## Prerequisites

Please apply this [schema](docs/schema.sql) to your SQL database.

## Entity Diagram

![ER Diagram](//www.plantuml.com/plantuml/png/SoWkIImgAStDuKhDAyaigLH8JKcEByjFJamgpKaigbIevb9Gq5B8JB5IA2ufoinBLx2n2V2simEBvYNcfiB4mG9PnVbvmSaPgRc9ACB9HQc99QafZYLM2ZdvO35TNQvQBeVKl1IWnG00)
<!--
```plantuml
@startuml
entity edc_catalog {
  * id: string <<PK>>
  --
}
@enduml
```
-->

## Configuration

| Key                                  | Description                                | Mandatory | 
|:-------------------------------------|:-------------------------------------------|---|
| edc.datasource.federatedCatalog.name | Datasource used to store federated catalog | X |
