import net.ltgt.gradle.errorprone.errorprone
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "2.4.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.asciidoctor.convert") version "1.5.8"
	id("java")
    jacoco
    id("com.palantir.baseline") version "3.69.0"
}

group = "li.selman"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

val snippetsDir by extra { file("build/generated-snippets") }

dependencies {
    // Database Access
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Web
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")

    // Monitoring
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Kotlin support TODO remove Kotlin support
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Dev tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    outputs.dir(snippetsDir)
}

// Disable error prone for test-sources
tasks.named<JavaCompile>("compileTestJava") {
    options.errorprone.isEnabled.set(false)
}

tasks.withType<JavaCompile>().configureEach {
    options.errorprone.disable("BracesRequired", "MissingSummary", "EqualsGetClass", "OptionalOrElseMethodInvocation",
        "PreferSafeLoggableExceptions", "PreferSafeLoggingPreconditions", "Slf4jConstantLogMessage",
        "StrictUnusedVariable" // Re-enable in the future
    )
}

tasks.withType<Javadoc>().configureEach {
    options.encoding = "UTF-8"
}

tasks.asciidoctor {
    inputs.dir(snippetsDir)
    dependsOn(tasks.test)
    // Define variable "snippets" to access the generated docs from /src/docs/asciidoc
    attributes(mapOf("snippets" to "${project.buildDir}/generated-snippets"))
}

// So we can access the docs from http://URL/docs/index.html when we build the jar.
// $ ./gradlew build
// $ java -jar build/libs/*.jar
// Visit: http://localhost:8888/docs/index.html
tasks.named<BootJar>("bootJar") {
    dependsOn(tasks.asciidoctor)
    from("${buildDir.name}/asciidoc/html5") {
        into("static/docs")
    }
}
