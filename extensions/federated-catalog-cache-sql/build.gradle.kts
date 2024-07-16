plugins {
    `java-library`
    id("com.gmv.inesdata.edc-application")
}

dependencies {
    api(project(":spi:federated-catalog-cache-spi"))
    implementation(libs.edc.federated.catalog.api)
    api(libs.edc.spi.core)
    api(libs.edc.transaction.spi)
    api(project(":extensions:inesdata-search-extension"))
    implementation(libs.edc.transaction.spi)
    implementation(libs.edc.transaction.datasource.spi)
    implementation(libs.edc.sql.core)
    implementation(libs.edc.lib.util)
    implementation(libs.edc.federated.catalog.core)
}


