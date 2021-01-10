plugins {
    kotlin("jvm") version "1.4.21"
    id("com.diffplug.spotless") version "5.9.0"
}

group = "com.github.thibseisel"
version = "1.0.0"

val kotestVersion = "4.3.2"
val ktlintVersion = "0.40.0"

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    compileOnly("io.github.java-diff-utils:java-diff-utils:4.9")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")
}

spotless {
    val editorConfig = mapOf(
        "max_line_length" to "100"
    )

    kotlin {
        ktlint(ktlintVersion).userData(editorConfig)
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint(ktlintVersion).userData(editorConfig)
    }
}

tasks.withType<Wrapper> {
    distributionType = Wrapper.DistributionType.ALL
}
