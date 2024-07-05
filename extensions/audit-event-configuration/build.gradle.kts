plugins {
    `java-library`
    id("com.gmv.inesdata.edc-application")
}

dependencies {
    api(libs.edc.auth.spi)

    implementation(libs.jakarta.rsApi)
    implementation(libs.edc.contract.spi)
    implementation(libs.edc.transfer.spi)

    testImplementation(libs.edc.core.junit)
    testImplementation(libs.assertj)
    testImplementation(libs.mockito.core)
}


