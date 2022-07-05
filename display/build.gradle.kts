import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.github.johnrengelman.shadow") version "7.1.2"

}

group = "scorpion"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)

                implementation("net.java.dev.jna:jna:5.7.0")
                implementation("com.alphacephei:vosk:0.3.38")
                implementation("com.google.code.gson:gson:2.8.9")
                implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
                implementation("software.amazon.awssdk:polly:2.17.209")
                implementation("javazoom:jlayer:1.0.1")
                implementation("org.reactivestreams:reactive-streams:1.0.4")
                implementation("software.amazon.awssdk:transcribestreaming:2.17.209")
                implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.0")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2-native-mt")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.6.2")


            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "display"
            packageVersion = "1.0.0"
        }
    }
}
