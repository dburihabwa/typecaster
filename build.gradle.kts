plugins {
    id("application")
    id("java")
    id("jacoco")
    id("org.sonarqube") version "4.0.0.2929"
}

group = "com.burihabwa"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
    testImplementation("org.assertj:assertj-core:3.23.1")
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