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