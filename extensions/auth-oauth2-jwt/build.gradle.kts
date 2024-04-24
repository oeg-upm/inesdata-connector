plugins {
    `java-library`
    id("com.gmv.inesdata.edc-application")
}

dependencies {
    api(libs.edc.auth.spi)
    // Temporal workaround to solve a vulnerability. Remove when using edc version > 0.6.0
    implementation(libs.edc.iam.oauth2.core) {
        exclude("org.bouncycastle", "bcprov-jdk18on")
    }
    implementation("org.bouncycastle:bcprov-jdk18on:1.78")        


    implementation(libs.jakarta.rsApi)

    testImplementation(libs.edc.core.junit)
    testImplementation(libs.assertj)
    testImplementation(libs.mockito.core)
}


