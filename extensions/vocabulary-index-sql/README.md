# SQL Vocabulary

Provides SQL persistence for vocabularies.

## Prerequisites

Please apply this [schema](docs/schema.sql) to your SQL database.

## Entity Diagram

![ER Diagram](//https://www.plantuml.com/plantuml/png/RSun2iCm38NXtQVGNCW5GWZ9MBeKUe2YsY9riIMGbO0flNj3GeT0r-_zmnkAeTgSaoEsQ1Ke-FiY7XzpGgtmTW0dYA65OXfvWgwxNlf-Ko_Cv4tq_7TcpFJplKUZIRGUy5M4R_v96O-jqbg7qLf8ibdJk8yRYCDwzWi0)
<!--
```plantuml
@startuml
entity edc_vocabulary {
  * id: string <<PK>>
  * connectorId: string <<PK>>
  * name: string
  * category: string
  * jsonSchema: string
  * createdAt: long
  --
}
@enduml
```
-->

## Configuration

| Key | Description | Mandatory | 
|:---|:---|---|
| edc.datasource.vocabulary.name | Datasource used to store vocabularies | X |
