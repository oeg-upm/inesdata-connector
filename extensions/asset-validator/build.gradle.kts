plugins {
    `java-library`
    id("com.gmv.inesdata.edc-application")
}

dependencies {
    api(libs.edc.api.core)

    implementation(libs.edc.control.plane.spi)
    implementation(libs.edc.lib.validator)
}


