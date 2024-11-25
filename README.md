# InesData Connector

Dataspaces connector for the InesData project, based on the EDC dataspaces framework

## Launching the connector

- Compile:
  ```
  ./gradlew launchers:connector:build
  ```

- Launch:
  ```
  java <opts> -jar launchers/connector/build/libs/connector-app.jar
  ```

  Example:
  ```
  java -Dedc.keystore=resources/certs/store.pfx -Dedc.keystore.password=<passwd> -Dedc.vault=resources/configuration/provider-vault.properties -Dedc.fs.config=resources/configuration/provider-configuration.properties -jar launchers/connector/build/libs/connector-app.jar
  ```

## Containerizing the connector

The connector is prepared to be executed inside a container. The following steps must be followed to generate the connector:
- Compile the connector:
  ```
  ./gradlew launchers:connector:build
  ```

- Create a local Docker image:
  ```
  docker build --tag inesdata/connector:0.2.0 --build-arg CONNECTOR_JAR=./launchers/connector/build/libs/connector-app.jar -f docker/Dockerfile .
  ```

## Database

The `resources/sql` folder contains all the required schemes to be applied in the connector database.
