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
    maven("https://jitpack.io")
}

dependencies {
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-reflect
    implementation(kotlin("reflect"))

    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web
    implementation("org.springframework.boot:spring-boot-starter-web:2.5.1")

    // https://github.com/DV8FromTheWorld/JDA/
    implementation("net.dv8tion:JDA:4.3.0_287") {
        exclude("opus-java")
    }

    // https://github.com/ruffrick/jda-commands
    implementation("com.github.ruffrick:jda-commands:f43ff07")

    // https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
    implementation("ch.qos.logback:logback-classic:1.2.3")

    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-configuration-processor
    kapt("org.springframework.boot:spring-boot-configuration-processor:2.5.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
