plugins {
    `java-library`
    id("com.gmv.inesdata.edc-application")
}

dependencies {
    api(libs.edc.spi.core)
    implementation(libs.edc.data.plane.public.api)
}
