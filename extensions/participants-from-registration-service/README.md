### Catalog Service Participants Extension

This extension has the functionality for getting participant data within a service catalog.

#### Overview

The `FromRegistrationServiceParticipantsExtension` class implements `ServiceExtension` to facilitate periodic updates of participant data from a registration service. It utilizes the Eclipse EDC framework for managing and processing metadata.

#### Features

- **Periodic Updates**: Periodically retrieves participant data from a configurable registration service.
- **Integration**: Integrates with Eclipse EDC for metadata management and service integration.
- **Concurrency**: Uses scheduled tasks for efficient and timely data updates.

#### Setup

1. **Dependencies**: Ensure dependencies like Eclipse EDC are included in the project.

2. **Configuration**: Adjust settings such as `edc.participants.cache.execution.period.seconds` based on operational requirements.

#### Usage

The extension initializes by retrieving participant configurations and scheduling periodic updates using an in-memory directory (`InMemoryNodeDirectory`). Participant data is obtained via HTTP GET requests and transformed into `TargetNode` objects for ingestion into the directory.

#### Components

- **ParticipantConfiguration**: Manages HTTP requests to the registration service and transforms responses into `TargetNode` objects.

- **SharedNodeDirectory**: Implements `TargetNodeDirectory` to maintain and update participant nodes across the application.
