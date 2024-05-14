# Vocabulary API

Provides a management API for work with vocabularies. This API expands the functionality of the control-plane management API to be able to handle Vocabulary entities.


## Vocabulary entity

An example of a Vocabulary entity is shown below.

```
{
  "@context": {
    "@vocab": "https://w3id.org/edc/v0.0.1/ns/"
  },
  "@id": "vocabularyId",
  "name": "Vocabulary name",
  "jsonSchema": "{ \"title\": \"vocabulary\", \"type\": \"object\", \"properties\": { \"name\": { \"type\": \"string\", \"title\": \"Name\" }, \"keyword\": { \"type\": \"array\", \"title\": \"Keywords\", \"items\": { \"type\": \"string\" } } }, \"required\": [ \"name\" ] }"
}
```
