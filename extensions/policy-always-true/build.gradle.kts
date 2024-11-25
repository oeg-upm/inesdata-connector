plugins {
    `java-library`
    id("com.gmv.inesdata.edc-application")
}

dependencies {
    api(libs.edc.spi.core)
    api(libs.edc.policy.engine.spi)
    api(libs.edc.control.plane.spi)
    implementation(libs.edc.api.core)
}
