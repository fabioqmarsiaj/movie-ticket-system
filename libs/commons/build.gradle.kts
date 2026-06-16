plugins {
    id("java-library")
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    implementation("jakarta.validation:jakarta.validation-api")
}

tasks {
    jar {
        archiveClassifier.set("")
        enabled = true
    }
}