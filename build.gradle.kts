import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val dusseldorfKtorVersion = "1.5.2.5b2dff7"
val ktorVersion = ext.get("ktorVersion").toString()
val kafkaEmbeddedEnvVersion = ext.get("kafkaEmbeddedEnvVersion").toString()
val kafkaVersion = ext.get("kafkaVersion").toString() // Alligned med version fra kafka-embedded-env
val brukernotifikasjonSchemaVersion = "1.2020.02.07-13.16-fa9d319688b1"
val confluentVersion = "5.2.0"

val mainClass = "no.nav.helse.OmsorgspengerMottakKt"


plugins {
    kotlin("jvm") version "1.5.20"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

buildscript {
    apply("https://raw.githubusercontent.com/navikt/dusseldorf-ktor/5b2dff7a11531e487ebdee06b6ae94e0e50287c2/gradle/dusseldorf-ktor.gradle.kts")
}

repositories {
    mavenLocal()

    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/navikt/dusseldorf-ktor")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }

    mavenCentral()
    maven("https://jitpack.io")
    maven("https://packages.confluent.io/maven/")
}


dependencies {
    // Server
    implementation("no.nav.helse:dusseldorf-ktor-core:$dusseldorfKtorVersion")
    implementation("no.nav.helse:dusseldorf-ktor-jackson:$dusseldorfKtorVersion")
    implementation("no.nav.helse:dusseldorf-ktor-metrics:$dusseldorfKtorVersion")
    implementation("no.nav.helse:dusseldorf-ktor-health:$dusseldorfKtorVersion")
    implementation("no.nav.helse:dusseldorf-ktor-auth:$dusseldorfKtorVersion")

    // Client
    implementation("no.nav.helse:dusseldorf-ktor-client:$dusseldorfKtorVersion")
    implementation("no.nav.helse:dusseldorf-oauth2-client:$dusseldorfKtorVersion")

    // Kafka
    implementation("org.apache.kafka:kafka-clients:$kafkaVersion")
    implementation("no.nav:brukernotifikasjon-schemas:$brukernotifikasjonSchemaVersion")
    implementation("io.confluent:kafka-avro-serializer:$confluentVersion")

    // Test
    testImplementation("no.nav:kafka-embedded-env:$kafkaEmbeddedEnvVersion")
    testImplementation("no.nav.helse:dusseldorf-test-support:$dusseldorfKtorVersion")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion") {
        exclude(group = "org.eclipse.jetty")
    }
    testImplementation("org.skyscreamer:jsonassert:1.5.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}


tasks.withType<ShadowJar> {
    archiveBaseName.set("app")
    archiveClassifier.set("")
    manifest {
        attributes(
            mapOf(
                "Main-Class" to mainClass
            )
        )
    }
}

tasks.withType<Wrapper> {
    gradleVersion = "7.0.1"
}