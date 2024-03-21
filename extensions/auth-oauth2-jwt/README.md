# Oauth2 JWT Token Authentication Service

This extension provides the capability to authorizate the request to the connector management API. The extension will access the Bearer token provided in the Authorization header and validate that it is a valid JWT-encoded bearer token. It is necessary to have the `org.eclipse.edc:oauth2-core` extension correctly configured.

## Configuration

Example configuration:

```properties
edc.api.auth.oauth2.allowedRoles.1.role=connector-admin
edc.api.auth.oauth2.allowedRoles.2.role=connector-management
```

The `edc.api.auth.oauth2.allowedRoles` will be used by the federated catalog to retrieve the list of allowed roles that can perform requests on the managemente API connector.