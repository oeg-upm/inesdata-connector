pluginManagement {
    // Include 'plugins build' to define convention plugins.
    includeBuild("build-logic")
}

rootProject.name = "inesdata-connector"

// Extensions
include(":extensions:participants-from-configuration")
include(":extensions:auth-oauth2-jwt")

// Connector
include(":launchers:connector")
