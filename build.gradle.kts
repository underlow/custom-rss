plugins {
    kotlin("jvm") version "1.3.61"
    application
    id("com.github.johnrengelman.shadow") version "5.0.0"
}

group = "me.underlow"
version = "1.0-SNAPSHOT"

val ktorVersion: String by extra { "1.3.0" }

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.ktor:ktor-server-core:${ktorVersion}")
    implementation("io.ktor:ktor-server-netty:${ktorVersion}")
    implementation("org.jsoup:jsoup:1.12.2")
    implementation("io.ktor:ktor-client-core:${ktorVersion}")
    implementation("com.rometools:rome:1.12.2")
    implementation("ch.qos.logback:logback-classic:1.3.0-alpha5")
}

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

tasks.withType<Jar> {
    archiveName = "application.jar"
    manifest {
        attributes(
            mapOf(
                "Main-Class" to application.mainClassName
            )
        )
    }
}

// required for heroku deploy
tasks.register("stage") {
    dependsOn("shadowJar")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
