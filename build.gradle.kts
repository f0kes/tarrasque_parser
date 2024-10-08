
plugins {
    id("java")
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.9.22"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.skadistats:clarity:3.0.0")
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.6.2")
    implementation("redis.clients:jedis:4.3.0") // Check for the latest version
    implementation("io.ktor:ktor-server-core:2.3.7")
    implementation("io.ktor:ktor-server-netty:2.3.7")
    implementation("io.ktor:ktor-server-host-common:2.3.7")
    implementation("io.ktor:ktor-server-status-pages:2.3.7")
    implementation("io.insert-koin:koin-core:3.5.0")
    implementation("io.insert-koin:koin-ktor:3.5.0")

}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}