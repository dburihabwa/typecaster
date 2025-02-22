package com.burihabwa.typecaster;

import org.apache.commons.cli.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws ParseException {
        Options options = new Options();
        options.addRequiredOption("p", "path", true, "Path to the project to analyze");
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        if (!cmd.hasOption("p")) {
            logger.warn("path argument is missing!");
            System.exit(0);
        }
        var path = Path.of(cmd.getOptionValue("path"));
        logger.atInfo().log("Analyzing project: %s".formatted(path.toAbsolutePath()));
        Configuration configuration = ConfigurationBuilder.newInstance()
                .setPath(path)
                .build();
        logger.atInfo().log(configuration.toString());
    }
}
