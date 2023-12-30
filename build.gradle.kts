plugins {
    kotlin("jvm") version "2.0.0-Beta2"
}

sourceSets {
    main {
        kotlin.srcDir("src/main/kotlin")
    }
}

tasks {
    wrapper {
        gradleVersion = "8.5"
    }
    test {
        useJUnit()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.google.guava:guava:32.0.1-jre")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
}