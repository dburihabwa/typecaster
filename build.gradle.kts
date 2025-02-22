plugins {
    id("application")
    id("java")
    id("jacoco")
    alias(libs.plugins.sonarqube)
}

group = "com.burihabwa"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.commons.cli)
    implementation(libs.logback.classic)
    implementation(libs.slf4j.api)

    testRuntimeOnly(libs.junit.jupiter.engine)

    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
}
repositories {
    mavenCentral()
}
application {
    mainClass.set("com.burihabwa.typecaster.Main")
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

jacoco {
    toolVersion = "0.8.8"
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(false)
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}

sonarqube {
    properties {
        property("sonar.projectKey", "dburihabwa_typecaster")
        property("sonar.organization", "dburihabwa")
        property ("sonar.host.url", "https://sonarcloud.io")
    }
}