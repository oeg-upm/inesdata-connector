plugins {
    `java-library`
    id("com.gmv.inesdata.edc-application")
}

dependencies {
    implementation(libs.edc.federated.catalog.api)
    api(libs.edc.spi.core)
    api(libs.edc.transaction.spi)
    implementation(libs.edc.transaction.spi)
    implementation(libs.edc.transaction.datasource.spi)
    implementation(libs.edc.sql.core)
    implementation(libs.edc.lib.util)
    implementation(libs.edc.federated.catalog.core)
}


