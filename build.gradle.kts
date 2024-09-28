import com.github.gradle.node.npm.task.NpxTask
import org.jmailen.gradle.kotlinter.tasks.FormatTask
import org.jmailen.gradle.kotlinter.tasks.LintTask

val kotlinTestVersion: String by project
val logbackVersion: String by project
val springContextVersion: String by project

plugins {
    idea
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20"
    id("io.ktor.plugin") version "3.0.0-rc-1"
    id("com.github.node-gradle.node") version "7.0.2"
    id("org.jmailen.kotlinter") version "4.4.1"
}

group = "com.github.sipe90"
version = "0.0.1"

kotlin {
    jvmToolchain(20)
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor Server
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-config-yaml")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")

    // Ktor Client
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-client-content-negotiation-jvm")

    // Kotlinx
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")

    // DI
    implementation("org.springframework:spring-context:$springContextVersion")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    // Test
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinTestVersion")
    testImplementation("io.ktor:ktor-server-test-host-jvm")
}

tasks.withType<LintTask> {
    this.source = this.source.minus(fileTree("${layout.buildDirectory.get()}/generated")).asFileTree
}

tasks.withType<FormatTask> {
    this.source = this.source.minus(fileTree("${layout.buildDirectory.get()}/generated")).asFileTree
}

node {
    download = true
    version = "20.17.0"
}

tasks.register("createGeneratedSourceFolders") {
    mkdir("${layout.buildDirectory.get()}/generated/src/main/kotlin/com/github/sipe90/lunchscraper/openapi")
}

tasks.register<NpxTask>("generateRestaurantExtractionModel") {
    dependsOn("createGeneratedSourceFolders")

    val inputFile = "src/main/resources/openai/restaurant_extraction_schema.json"
    val outputFile = "${layout.buildDirectory.get()}/generated/src/main/kotlin/com/github/sipe90/lunchscraper/openapi/RestaurantExtractionResult.kt"

    inputs.file(inputFile)
    outputs.file(outputFile)

    command = "quicktype"
    args = listOf(
        "--src-lang", "schema",
        "--out", outputFile,
        "--framework", "kotlinx",
        "--package", "com.github.sipe90.lunchscraper.openapi",
        inputFile
    )
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
        "--framework", "kotlinx",
        "--package", "com.github.sipe90.lunchscraper.openapi",
        inputFile
    )
}

sourceSets.main.configure {
    kotlin.srcDirs("${layout.buildDirectory.get()}/generated/src/main/kotlin")
}

tasks.getByName("compileKotlin").dependsOn("generateRestaurantExtractionModel", "generateMenuExtractionModel")
