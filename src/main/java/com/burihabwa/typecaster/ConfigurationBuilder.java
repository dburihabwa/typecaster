package com.burihabwa.typecaster;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigurationBuilder {
    private static final Pattern GRADLE_SOURCE_COMPATIBILITY = Pattern.compile("sourceCompatibility\\s*=\\s*[\'\"](?<version>\\d+)[\'\"]");
    private static final Logger LOG = Logger.getLogger(ConfigurationBuilder.class.getCanonicalName());
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
        BuildSystem buildSystem = getBuildSystem();
        return new Configuration(
                buildSystem,
                getJavaVersion(buildSystem)
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

    private JavaVersion getJavaVersion(BuildSystem buildSystem)  {
        if (buildSystem.equals(BuildSystem.MAVEN)) {
            Path model = path.resolve("pom.xml");
            return getJavaVersionFromPomXml(model);
        } else if (buildSystem.equals(BuildSystem.GRADLE)) {
            Path model = path.resolve("build.gradle");
            return getJavaVersionFromBuildGradle(model);
        }
        return JavaVersion.UNDETERMINED;
    }

    private static JavaVersion getJavaVersionFromBuildGradle(Path model) {
        String data;
        try (FileInputStream in = new FileInputStream(model.toFile())) {
            data = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOG.warning(e::getMessage);
            return JavaVersion.UNDETERMINED;
        }
        Matcher matcher = GRADLE_SOURCE_COMPATIBILITY.matcher(data);
        if (matcher.find()) {
            return match(matcher.group("version"));
        }
        return JavaVersion.UNDETERMINED;
    }

    private static JavaVersion getJavaVersionFromPomXml(Path model) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(model.toFile());
            NodeList properties = document.getElementsByTagName("properties");
            if (properties == null || properties.getLength() == 0) {
                LOG.log(Level.WARNING, () -> String.format("Could not find properties tag in %s.", model));
                return JavaVersion.UNDETERMINED;
            }
            Element propertiesElement = (Element) properties.item(0);
            NodeList childNodes = propertiesElement.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    if (element.getTagName().equals("maven.compiler.source") && element.hasChildNodes()) {
                        return match(element.getChildNodes().item(0).getNodeValue());
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOG.log(Level.WARNING, e::getMessage);
        }
        return JavaVersion.UNDETERMINED;
    }

    private static JavaVersion match(String value){
        if (value == null) {
            return JavaVersion.UNDETERMINED;
        }
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "8" -> JavaVersion.JAVA_8;
            case "11" -> JavaVersion.JAVA_11;
            case "17" -> JavaVersion.JAVA_17;
            default -> JavaVersion.UNDETERMINED;
        };
    }
}
