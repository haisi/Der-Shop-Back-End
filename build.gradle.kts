import net.ltgt.gradle.errorprone.errorprone
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "2.4.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.asciidoctor.jvm.convert") version "3.1.0"
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

dependencies {
    // Database Access
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.liquibase:liquibase-core")
//    implementation("org.liquibase:liquibase-hibernate5")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Web
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    runtimeOnly("org.springframework.data:spring-data-rest-hal-explorer")

    // Security
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt-api:0.11.2")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")
    // Uncomment the next line if you want to use RSASSA-PSS (PS256, PS384, PS512) algorithm
//    runtimeOnly("org.bouncycastle:bcprov-jdk15on:1.60")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.2")

    // Monitoring
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Dev tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    implementation("org.jetbrains:annotations:20.1.0")

    // Test
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
}

val snippetsDir by extra { file("build/generated-snippets") }

spotless {
    java {
        target(fileTree("src/**/*.java") {
            exclude(
                // Manual source copy of apache-commons-lang3
                "**/li/selman/dershop/app/security/RandomStringUtils.java",
                // Manual source copies of certain classes from https://github.com/jhipster/jhipster-bom
                "**/li/selman/dershop/app/security/RandomUtil.java",
                "**/li.selman.dershop.app.persistence/AsyncSpringLiquibase.java",
                "**/li.selman.dershop.app.persistence/LiquibaseConfiguration.java",
                "**/li.selman.dershop.app.persistence/SpringLiquibaseUtil.java"
            )
        })
    }
}

java {
    withJavadocJar()
}

asciidoctorj {
    modules {
        diagram.use()
        diagram.setVersion("2.0.5")
    }
}

tasks.named<org.asciidoctor.gradle.jvm.AsciidoctorTask>("asciidoctor") {
    // By default the output dir is "build/docs/asciidoc".
    // For github-pages we expose the "docs" root directory.
    // However, we want to use the asciidoc as the index page of the website.
    setOutputDir(file("build/docs"))
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
        "SameNameButDifferent", // Due to Lombok
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
// $ find build/libs/*.jar -type f -not -name "*javadoc*" | xargs java -jar
// Visit: http://localhost:8888/docs/index.html
tasks.named<BootJar>("bootJar") {
    dependsOn(tasks.asciidoctor)
    from("${buildDir.name}/docs") {
        into("static/docs")
    }
}
