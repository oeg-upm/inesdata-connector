plugins {
    `java-library`
    id("com.gmv.inesdata.edc-application")
}

dependencies {
    api(project(":spi:count-elements-spi"))
    implementation(project(":extensions:count-elements-api"))
    api(libs.edc.spi.core)
    api(libs.edc.transaction.spi)
    implementation(libs.edc.transaction.spi)
    implementation(libs.edc.transaction.datasource.spi)
    implementation(libs.edc.sql.core)
    implementation(libs.edc.lib.util)
}


