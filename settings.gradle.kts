pluginManagement {
    // Include 'plugins build' to define convention plugins.
    includeBuild("build-logic")
}

rootProject.name = "inesdata-connector"

// SPI 
include(":spi:vocabulary-spi")
include(":spi:federated-catalog-cache-spi")

// Extensions
include(":extensions:auth-oauth2-jwt")
include(":extensions:participants-from-configuration")
include(":extensions:policy-always-true")
include(":extensions:policy-time-interval")
include(":extensions:vocabulary-api")
include(":extensions:vocabulary-index-sql")
include(":extensions:store-asset-api")
include(":extensions:federated-catalog-cache-sql")
include(":extensions:federated-catalog-cache-api")

// Connector
include(":launchers:connector")
