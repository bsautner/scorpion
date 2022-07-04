import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.0"
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "scorpion"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation("net.java.dev.jna:jna:5.7.0")
    implementation("com.alphacephei:vosk:0.3.38")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2-native-mt")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.6.2")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}