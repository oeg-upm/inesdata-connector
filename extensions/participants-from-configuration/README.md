# InesData Connector

This extension provides the capability to obtain the list of participants of a data space in order to allow the federated catalog to obtain their data offerings.

## Configuration

Example configuration:

```properties
edc.catalog.configuration.participant.list=100210;connector-company-sample;http://targe-url-cs:9194/protocol|561349;connector-company-example;http://targe-url-ce:9194/protocol
```

The `edc.catalog.configuration.participant.list` will be used by the federated catalog to retrieve the list of participants whose catalog will be obtained for federating it.