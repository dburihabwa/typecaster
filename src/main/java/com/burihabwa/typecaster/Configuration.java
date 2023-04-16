package com.burihabwa.typecaster;

import java.nio.file.Path;
import java.util.List;

public record Configuration(BuildSystem buildSystem, JavaVersion version, List<Path> sourceDirectories) {
    static ConfigurationBuilder newBuilder() {
        return ConfigurationBuilder.newInstance();
    }
}
