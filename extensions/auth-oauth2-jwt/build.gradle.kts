plugins {
    `java-library`
    id("com.gmv.inesdata.edc-application")
}

dependencies {
    api(libs.edc.auth.spi)
    
    implementation(libs.edc.iam.oauth2.core)     
    implementation(libs.jakarta.rsApi)

    testImplementation(libs.edc.core.junit)
    testImplementation(libs.assertj)
    testImplementation(libs.mockito.core)
}


