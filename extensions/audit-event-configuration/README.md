# AuditEventSubscriptionExtension

This extension provides the capability to subscribe to audit events related to contract negotiations and transfer processes. The `AuditEventSubscriber` logs details about these events using the provided monitoring interface.

## Features

- Logs audit details for contract negotiation and transfer process events.
- Registers the `AuditEventSubscriber` with the event router for both asynchronous and synchronous event handling.

## Configuration

To configure the `AuditEventSubscriptionExtension`, ensure that the `AuditEventSubscriber` is registered with the event router. This is done within the `AuditEventSubscriptionExtension` class.


