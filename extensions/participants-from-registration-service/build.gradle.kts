plugins {
    `java-library`
    id("com.gmv.inesdata.edc-application")
}

dependencies {
    implementation(libs.edc.federated.catalog.spi)
    implementation(libs.edc.federated.catalog.core)
    implementation(libs.ok.http3)
    implementation(libs.edc.http.spi)
    implementation(libs.edc.identity.did.spi)

    testImplementation(libs.edc.core.junit)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.assertj)
    testImplementation(libs.mockito.core)
}
