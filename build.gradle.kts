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
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
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
