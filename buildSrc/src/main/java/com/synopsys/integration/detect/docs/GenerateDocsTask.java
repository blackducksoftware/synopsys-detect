/*
 * buildSrc
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.docs;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

import com.google.gson.Gson;
import com.synopsys.integration.detect.docs.copied.HelpJsonData;
import com.synopsys.integration.detect.docs.copied.HelpJsonOption;
import com.synopsys.integration.detect.docs.model.DeprecatedPropertyTableGroup;
import com.synopsys.integration.detect.docs.model.Detector;
import com.synopsys.integration.detect.docs.model.DetectorStatusCodes;
import com.synopsys.integration.detect.docs.model.SimplePropertyTableGroup;
import com.synopsys.integration.detect.docs.model.SplitGroup;
import com.synopsys.integration.detect.docs.pages.AdvancedPropertyTablePage;
import com.synopsys.integration.detect.docs.pages.DeprecatedPropertyTablePage;
import com.synopsys.integration.detect.docs.pages.DetectorsPage;
import com.synopsys.integration.detect.docs.pages.ExitCodePage;
import com.synopsys.integration.detect.docs.pages.SimplePropertyTablePage;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;

import freemarker.template.Template;
import freemarker.template.TemplateException;

public class GenerateDocsTask extends DefaultTask {
    public static final String NAVIGATION_FILENAME = "nav.md";
    private final IntLogger logger = new Slf4jIntLogger(this.getLogger());

    @TaskAction
    public void generateDocs() throws IOException, TemplateException, IntegrationException {
        Project project = getProject();
        File file = new File("synopsys-detect-" + project.getVersion() + "-help.json");
        Reader reader = new FileReader(file);
        HelpJsonData helpJson = new Gson().fromJson(reader, HelpJsonData.class);
        File docsDir = project.file("docs");
        File outputDir = project.file("docs/generated");
        File runningDir = new File(outputDir, "downloadingandrunning");
        File troubleshootingDir = new File(outputDir, "troubleshooting");

        FileUtils.deleteDirectory(outputDir);
        troubleshootingDir.mkdirs();

        FileUtils.copyFile(new File(docsDir, NAVIGATION_FILENAME), new File(outputDir, NAVIGATION_FILENAME));
        TemplateProvider templateProvider = new TemplateProvider(project.file("docs/templates"), project.getVersion().toString());
        createFromFreemarker(templateProvider, troubleshootingDir, "exit-codes", new ExitCodePage(helpJson.getExitCodes()));
        createFromFreemarker(templateProvider, runningDir, "status-file", new DetectorStatusCodes(helpJson.getDetectorStatusCodes()));
        handleDetectors(templateProvider, outputDir, helpJson);
        handleProperties(templateProvider, outputDir, helpJson);
        handleContent(outputDir, templateProvider);
    }

    private void handleContent(File outputDir, TemplateProvider templateProvider) throws IOException, TemplateException {
        Project project = getProject();
        File templatesDir = new File(project.getProjectDir(), "docs/templates");
        File contentDir = new File(templatesDir, "content");

        // TODO: Not sure this method of tree walking actually works.
        try (Stream<Path> paths = Files.walk(contentDir.toPath())) {
            List<Path> foundFreemarkerFiles = paths.filter(it -> FilenameUtils.isExtension(it.getFileName().toString(), "ftl"))
                                                        .collect(Collectors.toList());

            for (Path foundFreemarkerFilePath : foundFreemarkerFiles) {
                createContentMarkdownFromTemplate(templatesDir, contentDir, foundFreemarkerFilePath.toFile(), outputDir, templateProvider);
            }
        }
    }

    private void createContentMarkdownFromTemplate(File templatesDir, File contentDir, File templateFile, File baseOutputDir, TemplateProvider templateProvider) throws IOException, TemplateException {
        String helpContentTemplateRelativePath = templatesDir.toPath().relativize(templateFile.toPath()).toString(); // TODO: Verify this is the correct substitution.
        File outputFile = deriveOutputFileForContentTemplate(contentDir, templateFile, baseOutputDir);
        logger.alwaysLog(String.format("Generating markdown from template file: %s --> %s", helpContentTemplateRelativePath, outputFile.getCanonicalPath()));
        createFromFreemarker(templateProvider, helpContentTemplateRelativePath, outputFile, new HashMap<String, String>());
    }

    private File deriveOutputFileForContentTemplate(File contentDir, File helpContentTemplateFile, File baseOutputDir) {
        String templateSubDir = contentDir.toPath().relativize(helpContentTemplateFile.toPath().getParent()).toString(); // TODO: Verify this is the correct substitution.
        File outputDir = new File(baseOutputDir, templateSubDir);
        String outputFileName = String.format("%s.md", FilenameUtils.removeExtension(helpContentTemplateFile.getName()));

        return new File(outputDir, outputFileName);
    }

    private void createFromFreemarker(TemplateProvider templateProvider, File outputDir, String templateName, Object data) throws IOException, TemplateException {
        createFromFreemarker(templateProvider, String.format("%s.ftl", templateName), new File(outputDir, String.format("%s.md", templateName)), data);
    }

    private void createFromFreemarker(TemplateProvider templateProvider, String templateRelativePath, File to, Object data) throws IOException, TemplateException {
        to.getParentFile().mkdirs();
        Template template = templateProvider.getTemplate(templateRelativePath);
        try (Writer writer = new FileWriter(to)) {
            template.process(data, writer);
        }
    }

    private void handleDetectors(TemplateProvider templateProvider, File baseOutputDir, HelpJsonData helpJson) throws IOException, TemplateException {
        File outputDir = new File(baseOutputDir, "components");
        List<Detector> build = helpJson.getBuildDetectors().stream()
                                         .map(Detector::new)
                                         .sorted(Comparator.comparing(Detector::getDetectorType).thenComparing(Detector::getDetectorName))
                                         .collect(Collectors.toList());

        List<Detector> buildless = helpJson.getBuildlessDetectors().stream()
                                             .map(Detector::new)
                                             .sorted(Comparator.comparing(Detector::getDetectorType).thenComparing(Detector::getDetectorName))
                                             .collect(Collectors.toList());

        createFromFreemarker(templateProvider, outputDir, "detectors", new DetectorsPage(buildless, build));
    }

    private String encodePropertyLocation(String propertyName) {
        if (!propertyName.equals(propertyName.trim())) {
            throw new RuntimeException("Property name should not include trim-able white space. Property (" + propertyName + ") should be shortened to (" + propertyName.trim() + ") ");
        }
        Map<String, String> supportedCharacters = new HashMap<>();
        String literals = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        for (char literalCharacter : literals.toCharArray()) {
            supportedCharacters.put(String.valueOf(literalCharacter), String.valueOf(literalCharacter));
        }
        supportedCharacters.put(" ", "-");
        supportedCharacters.put(".", "46");
        supportedCharacters.put("(", "40");
        supportedCharacters.put(")", "41");
        StringBuilder encoded = new StringBuilder();
        for (char character : propertyName.toCharArray()) {
            String charString = String.valueOf(character);
            if (!supportedCharacters.containsKey(charString)) {
                throw new RuntimeException("Unsupported character literal in property name, please add it to supported characters or remove the character (" + character + ") in (" + propertyName + ") ");
            } else {
                encoded.append(supportedCharacters.get(charString));
            }
        }
        return encoded.toString().toLowerCase();
    }

    private void handleProperties(TemplateProvider templateProvider, File outputDir, HelpJsonData helpJson) throws IntegrationException, IOException, TemplateException {
        Map<String, String> superGroups = createSuperGroupLookup(helpJson);

        // example: superGroup/key
        Map<String, String> groupLocations = superGroups.entrySet().stream()
                                                       .collect(Collectors.toMap(Map.Entry::getKey, it -> String.format("%s/%s", it.getValue(), it.getKey()).toLowerCase()));

        // Updating the location on all the json options so that a new object with only 1 new property did not have to be created (and then populated) from the existing.
        for (HelpJsonOption helpJsonOption : helpJson.getOptions()) {
            String groupLocation = getGroupLocation(groupLocations, helpJsonOption.getGroup());
            String encodedPropertyLocation = encodePropertyLocation(helpJsonOption.getPropertyName());
            helpJsonOption.setLocation(String.format("%s/#%s", groupLocation, encodedPropertyLocation)); //ex: superGroup/key/#property_name
        }

        Map<String, List<HelpJsonOption>> groupedOptions = helpJson.getOptions().stream()
                                                                     .collect(Collectors.groupingBy(HelpJsonOption::getGroup));

        List<SplitGroup> splitGroupOptions = new ArrayList<>();
        for (Map.Entry<String, List<HelpJsonOption>> group : groupedOptions.entrySet()) {
            List<HelpJsonOption> deprecated = group.getValue().stream()
                                                        .filter(HelpJsonOption::getDeprecated)
                                                        .collect(Collectors.toList());

            List<HelpJsonOption> simple = group.getValue().stream()
                                                    .filter(helpJsonObject -> !deprecated.contains(helpJsonObject) && (StringUtils.isBlank(helpJsonObject.getCategory()) || "simple".equals(helpJsonObject.getCategory())))
                                                    .collect(Collectors.toList());

            List<HelpJsonOption> advanced = group.getValue().stream()
                                                      .filter(it -> !simple.contains(it) && !deprecated.contains(it))
                                                      .collect(Collectors.toList());

            String superGroupName = getOrThrow(superGroups, group.getKey(), String.format("Missing super group: %s", group.getKey()));
            String groupLocation = getGroupLocation(groupLocations, group.getKey());

            // TODO: We only have to do this because we added "(Advanced)" to the section header of advanced properties. Putting property info in the name is not scalable and leads to fixes like this.
            advanced.forEach(property -> property.setLocation(String.format("%s-advanced", property.getLocation())));
            deprecated.forEach(property -> property.setLocation(String.format("%s-deprecated", property.getLocation())));

            SplitGroup splitGroup = new SplitGroup(group.getKey(), superGroupName, groupLocation, simple, advanced, deprecated);
            splitGroupOptions.add(splitGroup);
        }

        File propertiesFolder = new File(outputDir, "properties");
        for (SplitGroup group : splitGroupOptions) {
            File superGroupFolder = new File(propertiesFolder, group.getSuperGroup().toLowerCase());
            File targetMarkdown = new File(superGroupFolder, group.getGroupName() + ".md");
            createFromFreemarker(templateProvider, "property-group.ftl", targetMarkdown, group);
        }

        List<SimplePropertyTableGroup> simplePropertyTableData = new ArrayList<>();
        for (SplitGroup splitGroupOption : splitGroupOptions) {
            if (splitGroupOption.getSimple().isEmpty()) {
                continue;
            }

            String groupLocation = getGroupLocation(groupLocations, splitGroupOption.getGroupName());
            SimplePropertyTableGroup simplePropertyTableGroup = new SimplePropertyTableGroup(splitGroupOption.getGroupName(), groupLocation, splitGroupOption.getSimple());
            simplePropertyTableData.add(simplePropertyTableGroup);
        }

        List<DeprecatedPropertyTableGroup> deprecatedPropertyTableData = new ArrayList<>();
        for (SplitGroup splitGroupOption : splitGroupOptions) {
            if (splitGroupOption.getDeprecated().isEmpty()) {
                continue;
            }

            String groupLocation = getGroupLocation(groupLocations, splitGroupOption.getGroupName());
            DeprecatedPropertyTableGroup deprecatedPropertyTableGroup = new DeprecatedPropertyTableGroup(splitGroupOption.getGroupName(), groupLocation, splitGroupOption.getDeprecated());
            deprecatedPropertyTableData.add(deprecatedPropertyTableGroup);
        }

        createFromFreemarker(templateProvider, propertiesFolder, "basic-properties", new SimplePropertyTablePage(simplePropertyTableData));
        createFromFreemarker(templateProvider, propertiesFolder, "deprecated-properties", new DeprecatedPropertyTablePage(deprecatedPropertyTableData));
        createFromFreemarker(templateProvider, propertiesFolder, "all-properties", new AdvancedPropertyTablePage(splitGroupOptions));
    }

    private String getGroupLocation(Map<String, String> groupLocationMap, String group) throws IntegrationException {
        return getOrThrow(groupLocationMap, group, String.format("Missing group location: %s", group));
    }

    private <K, V> V getOrThrow(Map<K, V> map, K key, String missingMessage) throws IntegrationException {
        return Optional.ofNullable(map.get(key))
                   .orElseThrow(() -> new IntegrationException(missingMessage));
    }

    // Technically each key has exactly 1 super key (but this is not enforced in the json) so here we check that assumption and return the mapping.
    // TODO: Add a new object to the helpJson which is a super key lookup so that the super key lookup is not just embedded in the object and then we don't have to do this at all.
    // TODO: Add the default "Configuration" to the help json instead of blank and having this populate.
    private Map<String, String> createSuperGroupLookup(HelpJsonData helpJson) {
        Map<String, String> lookup = new HashMap<>();

        helpJson.getOptions().forEach(option -> {
            final String defaultSuperGroup = "Configuration";
            String rawSuperGroup = option.getSuperGroup();
            String superGroup;
            if (StringUtils.isBlank(rawSuperGroup)) {
                superGroup = defaultSuperGroup;
            } else {
                superGroup = rawSuperGroup;
            }

            if (lookup.containsKey(option.getGroup()) && !superGroup.equals(lookup.get(option.getGroup()))) {
                throw new RuntimeException(String.format("The created detect help JSON had a key '%s' whose super key '%s' did not match a different options super key in the same key '%s'.",
                    option.getGroup(),
                    superGroup,
                    lookup.get(option.getGroup())
                ));
            } else if (!lookup.containsKey(option.getGroup())) {
                lookup.put(option.getGroup(), superGroup);
            }
        });

        return lookup;
    }
}

