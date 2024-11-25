plugins {
    `java-library`
    id("com.gmv.inesdata.edc-application")
}

dependencies {
    api(project(":spi:federated-catalog-cache-spi"))
    api(project(":extensions:complex-policy-api"))
    api(libs.edc.spi.core)
    implementation(libs.edc.spi.transform)
    implementation(libs.edc.web.spi)

    implementation(libs.edc.connector.core)
    implementation(libs.edc.api.core)
    implementation(libs.edc.lib.util)
    implementation(libs.edc.lib.transform)
    implementation(libs.edc.dsp.api.configuration)
    implementation(libs.edc.api.management.config)
    implementation(libs.swagger.annotations.jakarta)
    implementation(libs.edc.transaction.spi)
    implementation(libs.edc.lib.validator)
    implementation(libs.edc.validator.spi)
    implementation(libs.edc.json.ld.lib)
    runtimeOnly(libs.edc.spi.jsonld)
    runtimeOnly(libs.edc.json.ld.lib)
    implementation(libs.edc.control.plane.transform)
    implementation(libs.edc.federated.catalog.api)
    implementation(libs.edc.federated.catalog.core)
    implementation(libs.edc.federated.catalog.spi)
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    api(libs.edc.policy.definition.api)

}
