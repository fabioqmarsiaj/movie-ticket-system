plugins {
	id("org.springframework.boot")
}

dependencies {
	implementation(project(":libs:commons"))

	implementation("org.springframework.boot:spring-boot-starter-kafka")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("com.fasterxml.jackson.core:jackson-databind")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3")

	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	testImplementation("org.springframework.boot:spring-boot-starter-kafka-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")

	testCompileOnly("org.projectlombok:lombok")
	testAnnotationProcessor("org.projectlombok:lombok")
}

tasks {
	bootJar {
		enabled = true
		mainClass.set("com.fabioqmarsiaj.movie.OrchestratorServiceApplication")
	}
	jar { enabled = false }
}