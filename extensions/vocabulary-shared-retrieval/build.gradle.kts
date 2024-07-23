plugins {
    `java-library`
    id("com.gmv.inesdata.edc-application")
}

dependencies {
    api(project(":extensions:participants-from-registration-service"))
    api(project(":spi:vocabulary-spi"))
    implementation(libs.edc.federated.catalog.spi)
    implementation(libs.edc.federated.catalog.core)
}