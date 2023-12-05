import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.9.21"
}

dependencies {
    testImplementation(kotlin("test"))
	testImplementation("junit:junit:4.13.2")
	testImplementation("com.google.guava:guava:31.1-jre")
}

tasks.test {
	useJUnit()
	testLogging {
		events("passed", "skipped", "failed")
	}
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}