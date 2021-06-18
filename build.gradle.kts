import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // https://plugins.gradle.org/plugin/org.springframework.boot
    id("org.springframework.boot") version "2.5.1"

    // https://plugins.gradle.org/plugin/org.jetbrains.kotlin.jvm
    kotlin("jvm") version "1.5.10"

    // https://plugins.gradle.org/plugin/org.jetbrains.kotlin.plugin.spring
    kotlin("plugin.spring") version "1.5.10"

    // https://plugins.gradle.org/plugin/org.jetbrains.kotlin.kapt
    kotlin("kapt") version "1.5.10"

    // https://plugins.gradle.org/plugin/org.jetbrains.kotlin.plugin.serialization
    kotlin("plugin.serialization") version "1.5.10"
}

group = "dev.ruffrick"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-stdlib
    implementation(kotlin("stdlib"))

    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-reflect
    implementation(kotlin("reflect"))

    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")

    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-serialization-json
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")

    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web
    implementation("org.springframework.boot:spring-boot-starter-web:2.5.1")

    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-actuator
    implementation("org.springframework.boot:spring-boot-starter-actuator:2.5.1")

    // https://github.com/DV8FromTheWorld/JDA/
    implementation("net.dv8tion:JDA:4.3.0_280") {
        exclude("opus-java")
    }

    // https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-api
    implementation("org.apache.logging.log4j:log4j-api:2.14.1")

    // https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-core
    implementation("org.apache.logging.log4j:log4j-core:2.14.1")

    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-configuration-processor
    kapt("org.springframework.boot:spring-boot-configuration-processor:2.5.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
