package com.synopsys.integration.detect.configuration.help.yaml;

import com.synopsys.integration.configuration.property.Property;
import com.synopsys.integration.detect.configuration.DetectInfoUtility;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.enumeration.DetectCategory;
import com.synopsys.integration.util.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HelpYamlWriter {
    private final Logger logger = LoggerFactory.getLogger(HelpYamlWriter.class);

    private static final Comparator<Property> SORT_BY_GROUP_THEN_KEY = (o1, o2) -> {
        if (o1.getPropertyGroupInfo().getPrimaryGroup().getName().equals(o2.getPropertyGroupInfo().getPrimaryGroup().getName())) {
            return o1.getKey().compareTo(o2.getKey());
        } else {
            return o1.getPropertyGroupInfo().getPrimaryGroup().getName().compareTo(o2.getPropertyGroupInfo().getPrimaryGroup().getName());
        }
    };

    public void createHelpYamlDocument(String filename) {
        List<Property> allProperties = DetectProperties.allProperties().getProperties();

        List<Property> filteredProperties = allProperties.stream()
                .filter(it -> (!it.isDeprecatedForRemoval() || it.getCategory() != DetectCategory.Advanced))
                .collect(Collectors.toList());

        File file1 = new File(filename);
        File buildDir = new File(file1.getParentFile(), "documentation/build");
        buildDir.mkdirs();
        File helpYaml = new File(buildDir, filename);

        try (FileWriter fileWriter = new FileWriter(helpYaml);
             BufferedWriter bufferWriter = new BufferedWriter(fileWriter)   ) {
            writeHeader(bufferWriter);
            writeProperties(bufferWriter, filteredProperties);
            logger.info("{} was created at {}", filename, helpYaml.getAbsolutePath());
        } catch (IOException e) {
            logger.info("There was an error creating the help yaml file.", e);
            throw new RuntimeException(e);
        }
    }
    private void writeHeader(BufferedWriter buffer) throws IOException {
        String bannerString = ResourceUtil.getResourceAsString(this.getClass(), "/banner.txt", StandardCharsets.UTF_8.toString());
        List<String> bannerStrings = Arrays.asList(bannerString.split("\n"));
        for (String line : bannerStrings) {
            buffer.write("# " + line);
            buffer.newLine();
        }

        String versionFileString = ResourceUtil.getResourceAsString(this.getClass(), "/version.txt", StandardCharsets.UTF_8.toString());
        List<String> versionFileContents = Arrays.asList(versionFileString.split("\n"));
        String versionText = DetectInfoUtility.parseValueFromVersionFileContents(versionFileContents, "version");
        buffer.write("# v" + versionText + "\n\n");

        String yamlInfoFileString = ResourceUtil.getResourceAsString(this.getClass(), "/help-yaml-header.txt", StandardCharsets.UTF_8.toString());
        buffer.write(yamlInfoFileString);
    }
    private void writeProperties(BufferedWriter buffer, List<Property> properties) throws IOException {
        List<Property> sortedProperties = properties.stream()
                .sorted(SORT_BY_GROUP_THEN_KEY)
                .collect(Collectors.toList());

        String group = null;
        for (Property p : sortedProperties) {
            String currentGroup = p.getPropertyGroupInfo().getPrimaryGroup().getName();
            if (group == null) {
                group = currentGroup;
                writeGroup(buffer, group);
            } else if (!group.equals(currentGroup)) {
                group = currentGroup;
                writeGroup(buffer, group);
            }
            writeProperty(buffer, p);
        }
    }
    private void writeGroup(BufferedWriter buffer, String groupName) throws IOException {
        buffer.write(String.format("\n\n##\n# %S\n##", groupName));
    }
    private void writeProperty(BufferedWriter buffer, Property property) throws IOException {
        buffer.write("\n#" + property.getKey() + ": ");
        if (property.describeDefault() != null) {
            buffer.write(property.describeDefault());
        }

        buffer.write("\n        # " + property.getPropertyHelpInfo().getShortText());

        List<String> acceptableValues = property.listExampleValues();
        if (!acceptableValues.isEmpty()) {
            buffer.write("\n        # Acceptable values:" + acceptableValues);
        }
    }
}


