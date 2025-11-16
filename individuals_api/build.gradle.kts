plugins {
    java
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.openapi.generator") version "7.17.0"
}

group = "org.orchestrator"
version = "0.0.1-SNAPSHOT"
description = "individuals_api"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(24)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    implementation("net.logstash.logback:logstash-logback-encoder:9.0")
    implementation("jakarta.validation:jakarta.validation-api:3.1.1")
    implementation("org.openapitools:jackson-databind-nullable:0.2.8")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.40")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("com.github.dasniko:testcontainers-keycloak:3.9.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

openApiGenerate {
    generatorName.set("spring")
    inputSpec.set("$rootDir/openapi/individuals_api_v1.0.yaml")
    outputDir.set("$buildDir/generated/individuals_api_v1.0")
    apiPackage.set("org.openapi.individuals.api")
    modelPackage.set("org.openapi.individuals.dto")
    configOptions.set(mapOf(
        "useSpringBoot3" to "true",
        "reactive" to "true",
    ))
}

sourceSets {
    main {
        java {
            srcDir("$buildDir/generated/individuals_api_v1.0/src/main/java")
        }
    }
}