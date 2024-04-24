plugins {
    `java-library`
    id("com.gmv.inesdata.edc-application")
}

dependencies {
    implementation(libs.edc.federated.catalog.spi)
    // Temporal workaround to solve a vulnerability. Remove when using edc version > 0.6.0
    implementation(libs.edc.federated.catalog.core) {
        exclude("org.bouncycastle", "bcprov-jdk18on")
    }
    implementation("org.bouncycastle:bcprov-jdk18on:1.78")        

    testImplementation(libs.edc.core.junit)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.assertj)
    testImplementation(libs.mockito.core)
}
