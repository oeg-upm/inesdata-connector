# Audit Extension

This extension provides the capability to log audit events for HTTP requests made to the connector management API. The `AuditExtension` registers an `HttpRequestInterceptor` that logs details about the user making the request and the request URI.

## Features

- Logs audit details for incoming HTTP requests.
- Extracts and verifies JWT tokens to log the username.
- Configurable participant ID for audit logging.

## Configuration

To configure the audit logging, you need to ensure that the `HttpRequestInterceptor` is registered with the web service. This is done within the `AuditExtension` class.
