package com.burihabwa.typecaster;

public record Configuration(BuildSystem buildSystem, JavaVersion version) {
    static ConfigurationBuilder newBuilder() {
        return ConfigurationBuilder.newInstance();
    }
}
