plugins {
    `java-library`
    id("com.gmv.inesdata.edc-application")
}

dependencies {
    implementation(libs.edc.federated.catalog.spi)
    implementation(libs.edc.federated.catalog.core)

}
