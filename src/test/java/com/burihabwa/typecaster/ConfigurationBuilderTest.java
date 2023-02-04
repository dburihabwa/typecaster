package com.burihabwa.typecaster;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConfigurationBuilderTest {
    private static final Path RESOURCES_PATH = Path.of("src", "test", "resources").toAbsolutePath();
    private static final Path EMPTY_PROJECT = RESOURCES_PATH.resolve("empty-project");

    @Test
    void build_throws_an_exception_when_the_path_is_not_set() {
        ConfigurationBuilder builder = ConfigurationBuilder.newInstance();
        assertThatThrownBy(() -> builder.build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Project path is not set.");
    }

    @Test
    void set_path_throws_an_IllegalArgumentException_when_path_is_not_a_directory() {
        ConfigurationBuilder builder = ConfigurationBuilder.newInstance();
        Path path = Path.of("non-existing-folder");
        assertThatThrownBy(() -> builder.setPath(path))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Project path is not set.");
    }

    @Test
    void build_set_sets_default_values_when_building_values() {
        ConfigurationBuilder builder = ConfigurationBuilder.newInstance();
        builder.setPath(EMPTY_PROJECT);
        Configuration build = builder.build();
        assertThat(build.buildSystem()).isEqualTo(BuildSystem.UNDETERMINED);
        assertThat(build.version()).isEqualTo(JavaVersion.UNDETERMINED);
    }

    @ParameterizedTest
    @MethodSource
    void build_set_values_proper_build_system(Path path, BuildSystem expectedBuildSystem) {
        Configuration build = ConfigurationBuilder.newInstance()
                .setPath(path)
                .build();
        assertThat(build.buildSystem()).isEqualTo(expectedBuildSystem);
    }


    private static Stream<Arguments> build_set_values_proper_build_system() {
        return Stream.of(
                Arguments.of(RESOURCES_PATH.resolve("empty-gradle-kts-project"), BuildSystem.GRADLE),
                Arguments.of(RESOURCES_PATH.resolve("empty-gradle-project"), BuildSystem.GRADLE),
                Arguments.of(RESOURCES_PATH.resolve("empty-maven-project"), BuildSystem.MAVEN),
                Arguments.of(RESOURCES_PATH.resolve("empty-project"), BuildSystem.UNDETERMINED)
        );
    }


    @ParameterizedTest(name = "{2}")
    @MethodSource("getProjectAndVersionPairs")
    void build_sets_correct_java_version(Path pathToProject, JavaVersion version, String testName) {
        Configuration build = ConfigurationBuilder.newInstance()
                .setPath(pathToProject)
                .build();
        assertThat(build.version()).isEqualTo(version);
    }

    private static Stream<Arguments> getProjectAndVersionPairs() {
        return Stream.of(
                Arguments.of(RESOURCES_PATH.resolve("empty-project"), JavaVersion.UNDETERMINED, "undetermined"),
                Arguments.of(RESOURCES_PATH.resolve("maven-java-8-project"), JavaVersion.JAVA_8, "Maven Java 8"),
                Arguments.of(RESOURCES_PATH.resolve("maven-java-11-project"), JavaVersion.JAVA_11, "Maven Java 11"),
                Arguments.of(RESOURCES_PATH.resolve("maven-java-17-project"), JavaVersion.JAVA_17, "Maven Java 17"),
                Arguments.of(RESOURCES_PATH.resolve("single-module-gradle-project"), JavaVersion.JAVA_17, "Gradle Java 17")
        );
    }
}