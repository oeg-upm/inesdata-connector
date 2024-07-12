plugins {
    `java-library`
    id("com.gmv.inesdata.edc-application")
}

dependencies {
    api(libs.edc.sql.core)
    implementation(libs.edc.web.spi)
}


