# InesData Connector

This extension provides the capability to obtain the list of participants of a data space in order to allow the federated catalog to obtain their data offerings.

## Configuration

Example configuration:

```properties
edc.catalog.configuration.participant.1.name = connector-c1
edc.catalog.configuration.participant.1.id = connector-c1
edc.catalog.configuration.participant.1.targetUrl = http://localhost:19194/protocol
edc.catalog.configuration.participant.2.name = connector-c2
edc.catalog.configuration.participant.2.id = connector-c2
edc.catalog.configuration.participant.2.targetUrl = http://localhost:29194/protocol
```

The `edc.catalog.configuration.participant` will be used by the federated catalog to retrieve the list of participants whose catalog will be obtained for federating it.