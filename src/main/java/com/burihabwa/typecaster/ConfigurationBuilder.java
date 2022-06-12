package com.burihabwa.typecaster;

import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigurationBuilder {
    private Path path;

    private ConfigurationBuilder() {
        /* Do nothing */
    }

    public static ConfigurationBuilder newInstance() {
        return new ConfigurationBuilder();
    }

    Configuration build() {
        if (path == null) {
            throw new IllegalStateException("Project path is not set.");
        }
        return new Configuration(
                getBuildSystem(),
                JavaVersion.UNDETERMINED
        );
    }

    public ConfigurationBuilder setPath(Path path) {
        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Project path is not set.");
        }
        this.path = path;
        return this;
    }

    private BuildSystem getBuildSystem() {
        if (Files.isRegularFile(path.resolve("build.gradle.kts"))) {
            return BuildSystem.GRADLE;
        }
        if (Files.isRegularFile(path.resolve("build.gradle"))) {
            return BuildSystem.GRADLE;
        }
        if (Files.isRegularFile(path.resolve("pom.xml"))) {
            return BuildSystem.MAVEN;
        }
        return BuildSystem.UNDETERMINED;
    }
}
