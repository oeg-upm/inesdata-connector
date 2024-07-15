# INESData search extension

This extension provides the capability to search inside the properties of an asset.
The functionality of this new search works as follows:
- To perform a search among the generic properties of the asset it is necessary to indicate 'genericSearch' as the value of the operandLeft
- To perform a search among the properties of a vocabulary, it is necessary to indicate 'https://w3id.org/edc/v0.0.1/ns/assetData' followed by the name of the vocabulary and the property to search for. An example is given in the following section.

## Example

```json
{
  "@context": {
    "@vocab": "https://w3id.org/edc/v0.0.1/ns/"
  },
  "offset": 0,
  "limit": 5,
  "sortOrder":  "ASC",
  "sortField": "id",
  "filterExpression": [
    {
      "operandLeft": "genericSearch",
      "operator": "LIKE",
      "operandRight": "%test%"
    },
    {
      "operandLeft": "'https://w3id.org/edc/v0.0.1/ns/assetData'.'https://w3id.org/edc/v0.0.1/ns/dcat-vocabulary'.'http://purl.org/dc/terms/language'",
      "operator": "=",
      "operandRight": "spanish"
    },
    {
      "operandLeft": "'https://w3id.org/edc/v0.0.1/ns/assetData'.'https://w3id.org/edc/v0.0.1/ns/dcat-vocabulary'.'http://purl.org/dc/terms/publisher'.'http://www.w3.org/2004/02/skos/core#notation'",
      "operator": "=",
      "operandRight": "notation-publisher"
    }
  ]
}
```
