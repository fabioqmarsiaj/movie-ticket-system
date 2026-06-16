plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "movie-ticket-system"

include("services:booking-service")
include("services:payment-service")
include("services:seat-service")
include("libs:commons")
