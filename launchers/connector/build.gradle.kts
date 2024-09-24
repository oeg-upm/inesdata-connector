plugins {
    `java-library`
    id("com.gmv.inesdata.edc-application")
    alias(libs.plugins.shadow)
}

dependencies {
    implementation(libs.edc.control.api.configuration)
    implementation(libs.edc.control.plane.api.client)
    implementation(libs.edc.control.plane.api)
    implementation(libs.edc.control.plane.core)
    implementation(libs.edc.dsp)
    implementation(libs.edc.http)
    implementation(libs.edc.configuration.filesystem)
    implementation(libs.edc.iam.mock)
    implementation(libs.edc.management.api)
    implementation(libs.edc.transfer.data.plane.signaling)
    implementation(libs.edc.transfer.pull.http.receiver)
    implementation(libs.edc.validator.data.address.http.data)

    implementation(libs.edc.edr.cache.api)
    implementation(libs.edc.edr.store.core)
    implementation(libs.edc.edr.store.receiver)

    implementation(libs.edc.data.plane.selector.api)
    implementation(libs.edc.data.plane.selector.core)

    implementation(libs.edc.data.plane.self.registration)
    implementation(libs.edc.data.plane.signaling.api)
    implementation(libs.edc.data.plane.public.api)
    implementation(libs.edc.data.plane.core)
    implementation(libs.edc.data.plane.http)
    implementation(libs.edc.data.plane.iam)
    //NUESTRO
    // Audit
    implementation(project(":extensions:audit-configuration"))
    // IAM Identity and authorization
    implementation(libs.edc.iam.oauth2.service)
    implementation(project(":extensions:auth-oauth2-jwt"))
    // Secretos
    implementation(libs.edc.vault.hashicorp)
    // Federated Catalog
    implementation(libs.edc.federated.catalog.spi)
    implementation(libs.edc.federated.catalog.core)
    implementation(libs.edc.federated.catalog.api)
    implementation(project(":extensions:federated-catalog-cache-sql"))
    implementation(project(":extensions:federated-catalog-cache-api"))

    // Count elements
    implementation(project(":extensions:count-elements-sql"))
    implementation(project(":extensions:count-elements-api"))

    //participants
    implementation(project(":extensions:participants-from-registration-service"))


    //Vocabulary
    implementation(project(":extensions:vocabulary-index-sql"))
    implementation(project(":extensions:vocabulary-api"))
    implementation(project(":extensions:vocabulary-shared-api"))
    implementation(project(":extensions:vocabulary-shared-retrieval"))

    // Policies
    implementation(project(":extensions:policy-always-true"))
    implementation(project(":extensions:policy-time-interval"))
    implementation(project(":extensions:policy-referring-connector"))
    implementation(project(":extensions:complex-policy-api"))
    // Storage assets
    implementation(project(":extensions:store-asset-api"))

    // Shared API
    implementation(project(":extensions:shared-api-configuration"))

    //Transfer
    implementation(project(":extensions:inesdata-transfer-process-api"))

    //Data plane public api
    implementation(project(":extensions:extended-data-plane-public-api"))

    //COUNT EDC LIBR
    implementation(libs.edc.spi.core)
    implementation(libs.edc.spi.transform)
    implementation(libs.edc.web.spi)
    implementation(libs.edc.api.core)
    implementation(libs.edc.lib.util)
    implementation(libs.edc.lib.transform)
    implementation(libs.edc.dsp.api.configuration)
    implementation(libs.edc.api.management.config)
    implementation(libs.swagger.annotations.jakarta)
    implementation(libs.edc.transaction.spi)
    implementation(libs.edc.lib.validator)
    implementation(libs.edc.validator.spi)
    implementation(libs.edc.spi.jsonld)
    implementation(libs.edc.transaction.datasource.spi)
    implementation(libs.edc.sql.core)

    //COUNT AÑADIENDO TRACTUS
    implementation(libs.edc.sql.contract.definition)
    implementation(libs.edc.sql.assetindex)
    implementation(libs.edc.sql.contract.negotiation)
    implementation(libs.edc.sql.transferprocess)
    implementation(libs.edc.sql.policydef)
    implementation(libs.edc.sql.pool)
    implementation(libs.edc.sql.policy.monitor)
    implementation(libs.edc.sql.edrindex)
    implementation(libs.edc.sql.accesstokendata)
    implementation(libs.edc.sql.dataplane)
    implementation(libs.postgres)
    implementation(libs.edc.transaction.local)
    implementation(libs.edc.spi.transaction.datasource)
    implementation(libs.edc.spi.transactionspi)


    implementation(libs.edc.data.plane.aws.s3)

}

application {
    mainClass.set("org.eclipse.edc.boot.system.runtime.BaseRuntime")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    mergeServiceFiles()
    archiveFileName.set("connector-app.jar")
}
