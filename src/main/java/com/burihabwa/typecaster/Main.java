package com.burihabwa.typecaster;

import org.apache.commons.cli.*;

import java.nio.file.Path;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getCanonicalName());

    public static void main(String[] args) throws ParseException {
        Options options = new Options();
        options.addRequiredOption("p", "path", true, "Path to the project to analyze");
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        if (!cmd.hasOption("p")) {
            logger.warning("path argument is missing!");
            System.exit(0);
        }
        var path = Path.of(cmd.getOptionValue("path"));
        Configuration configuration = ConfigurationBuilder.newInstance()
                .setPath(path)
                .build();
    }
}
