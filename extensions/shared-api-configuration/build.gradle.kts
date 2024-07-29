plugins {
    `java-library`
    id("com.gmv.inesdata.edc-application")
}

dependencies {
    api(libs.edc.web.spi)
    api(libs.edc.auth.spi)
    api(libs.edc.iam.oauth2.service)
    api(libs.edc.spi.jsonld)
    api(libs.edc.jersey.providers.lib)
}