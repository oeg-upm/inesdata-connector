pluginManagement {
    // Include 'plugins build' to define convention plugins.
    includeBuild("build-logic")
}

rootProject.name = "inesdata-connector"

// Extensions
include(":extensions:participants-from-configuration")

// Connector
include(":launchers:connector")
