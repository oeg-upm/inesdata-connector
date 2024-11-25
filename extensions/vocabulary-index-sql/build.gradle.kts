plugins {
    `java-library`
    id("com.gmv.inesdata.edc-application")
}

dependencies {
    api(project(":spi:vocabulary-spi"))
    implementation(project(":extensions:vocabulary-api"))
    api(libs.edc.spi.core)
    api(libs.edc.transaction.spi)
    implementation(libs.edc.transaction.spi)
    implementation(libs.edc.transaction.datasource.spi)
    implementation(libs.edc.sql.core)
    implementation(libs.edc.lib.util)
}


