
val kotlinTestVersion: String by project
val logbackVersion: String by project
val springContextVersion: String by project

plugins {
    idea
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20"
    id("io.ktor.plugin") version "3.0.0-rc-1"
    id("org.jmailen.kotlinter") version "4.4.1"
}

group = "com.github.sipe90"
version = "0.0.1"

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
