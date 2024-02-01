pluginManagement {
    // Include 'plugins build' to define convention plugins.
    includeBuild("build-logic")
}

rootProject.name = "inesdata-connector"

include(":base")
include(":launchers:connector")
