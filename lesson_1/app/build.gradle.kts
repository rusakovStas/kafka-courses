
plugins {
    id("java")
    id("application")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "ydx.practicum"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.kafka:kafka-clients:3.4.0")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("ydx.practicum.Main") // замените на название вашего главного класса
}