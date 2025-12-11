import com.github.gradle.node.npm.task.NpxTask
import io.ktor.plugin.features.DockerImageRegistry
import org.jmailen.gradle.kotlinter.tasks.FormatTask
import org.jmailen.gradle.kotlinter.tasks.LintTask

plugins {
    idea
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.node)
    alias(libs.plugins.release)
    alias(libs.plugins.kotlinter)
}

group = "com.github.sipe90"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

ktor {
    docker {
        jreVersion.set(JavaVersion.VERSION_21)
        localImageName.set(project.name)
        imageTag.set(project.version.toString())
        externalRegistry.set(
            DockerImageRegistry.dockerHub(
                appName = provider { project.name },
                username = providers.environmentVariable("DOCKERHUB_USERNAME"),
                password = providers.environmentVariable("DOCKERHUB_PASSWORD")
            )
        )
    }

}

release {
    tagTemplate.set("v\$version")
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    // Ktor Server
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.di)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)

    // Ktor Client
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)

    // Kotlinx
    implementation(libs.kotlinx.datetime)

    // DB
    implementation(libs.mongodb.driver.kotlin.coroutine)
    implementation(libs.bson)
    implementation(libs.bson.kotlinx)

    // Task scheduling
    implementation(libs.kt.scheduler)
    implementation(libs.cron.utils)

    // OpenAI
    implementation(libs.openai.client)

    // Flexmark
    implementation(libs.flexmark)

    // Logging
    implementation(libs.kotlin.logging.jvm)
    implementation(libs.logback.classic)

    // Test
    testImplementation(libs.bundles.junit)
    testImplementation(libs.ktor.server.test.host)
}

tasks.withType<LintTask> {
    mustRunAfter("generateMenuExtractionModel")
    exclude { it.file.path.contains("${File.separator}generated${File.separator}") }
}

tasks.withType<FormatTask> {
    exclude { it.file.path.contains("${File.separator}generated${File.separator}") }
}

node {
    download = true
    version = "22.19.0"
}

tasks.register("createGeneratedSourceFolders") {
    mkdir("${layout.buildDirectory.get()}/generated/src/main/kotlin/com/github/sipe90/lunchscraper/openapi")
}

tasks.register<NpxTask>("generateMenuExtractionModel") {
    dependsOn("createGeneratedSourceFolders")

    val inputFile = "src/main/resources/openai/menu_extraction_schema.json"
    val outputFile = "${layout.buildDirectory.get()}/generated/src/main/kotlin/com/github/sipe90/lunchscraper/openapi/MenuExtractionResult.kt"

    inputs.file(inputFile)
    outputs.file(outputFile)

    command = "quicktype"
    args = listOf(
        "--src-lang", "schema",
        "--out", outputFile,
        "--lang", "kotlin",
        "--framework", "kotlinx",
        "--package", "com.github.sipe90.lunchscraper.openapi",
        inputFile
    )
}

sourceSets.main.configure {
    kotlin.srcDirs("${layout.buildDirectory.get()}/generated/src/main/kotlin")
}

tasks.getByName("compileKotlin").dependsOn("generateMenuExtractionModel")
tasks.getByName("afterReleaseBuild").dependsOn("publishImage")

tasks.test {
    useJUnitPlatform()
}
