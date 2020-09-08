/**
 * buildSrc
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
    private final IntLogger logger = new Slf4jIntLogger(this.getLogger());

    @TaskAction
    public void generateDocs() throws IOException, TemplateException, IntegrationException {
        final Project project = getProject();
        final File file = new File("synopsys-detect-" + project.getVersion() + "-help.json");
        final Reader reader = new FileReader(file);
        final HelpJsonData helpJson = new Gson().fromJson(reader, HelpJsonData.class);

        final File outputDir = project.file("docs/generated");
        final File troubleshootingDir = new File(outputDir, "advanced/troubleshooting");

        FileUtils.deleteDirectory(outputDir);
        troubleshootingDir.mkdirs();

        final TemplateProvider templateProvider = new TemplateProvider(project.file("docs/templates"), project.getVersion().toString());

        createFromFreemarker(templateProvider, troubleshootingDir, "exit-codes", new ExitCodePage(helpJson.getExitCodes()));

        handleDetectors(templateProvider, outputDir, helpJson);
        handleProperties(templateProvider, outputDir, helpJson);
        handleContent(outputDir, templateProvider);
    }

    private void handleContent(final File outputDir, final TemplateProvider templateProvider) throws IOException, TemplateException {
        final Project project = getProject();
        final File templatesDir = new File(project.getProjectDir(), "docs/templates");
        final File contentDir = new File(templatesDir, "content");

        // TODO: Not sure this method of tree walking actually works.
        try (final Stream<Path> paths = Files.walk(contentDir.toPath())) {
            final List<Path> foundFreemarkerFiles = paths.filter(it -> FilenameUtils.isExtension(it.getFileName().toString(), "ftl"))
                                                        .collect(Collectors.toList());

            for (final Path foundFreemarkerFilePath : foundFreemarkerFiles) {
                createContentMarkdownFromTemplate(templatesDir, contentDir, foundFreemarkerFilePath.toFile(), outputDir, templateProvider);
            }
        }
    }

    private void createContentMarkdownFromTemplate(final File templatesDir, final File contentDir, final File templateFile, final File baseOutputDir, final TemplateProvider templateProvider) throws IOException, TemplateException {
        final String helpContentTemplateRelativePath = templatesDir.toPath().relativize(templateFile.toPath()).toString(); // TODO: Verify this is the correct substitution.
        final File outputFile = deriveOutputFileForContentTemplate(contentDir, templateFile, baseOutputDir);
        logger.alwaysLog(String.format("Generating markdown from template file: %s --> %s", helpContentTemplateRelativePath, outputFile.getCanonicalPath()));
        createFromFreemarker(templateProvider, helpContentTemplateRelativePath, outputFile, new HashMap<String, String>());
    }

    private File deriveOutputFileForContentTemplate(final File contentDir, final File helpContentTemplateFile, final File baseOutputDir) {
        final String templateSubDir = contentDir.toPath().relativize(helpContentTemplateFile.toPath().getParent()).toString(); // TODO: Verify this is the correct substitution.
        final File outputDir = new File(baseOutputDir, templateSubDir);
        final String outputFileName = String.format("%s.md", FilenameUtils.removeExtension(helpContentTemplateFile.getName()));

        return new File(outputDir, outputFileName);
    }

    private void createFromFreemarker(final TemplateProvider templateProvider, final File outputDir, final String templateName, final Object data) throws IOException, TemplateException {
        createFromFreemarker(templateProvider, String.format("%s.ftl", templateName), new File(outputDir, String.format("%s.md", templateName)), data);
    }

    private void createFromFreemarker(final TemplateProvider templateProvider, final String templateRelativePath, final File to, final Object data) throws IOException, TemplateException {
        to.getParentFile().mkdirs();
        final Template template = templateProvider.getTemplate(templateRelativePath);
        try (final Writer writer = new FileWriter(to)) {
            template.process(data, writer);
        }
    }

    private void handleDetectors(final TemplateProvider templateProvider, final File baseOutputDir, final HelpJsonData helpJson) throws IOException, TemplateException {
        final File outputDir = new File(baseOutputDir, "components");
        final List<Detector> build = helpJson.getBuildDetectors().stream()
                                         .map(Detector::new)
                                         .sorted(Comparator.comparing(Detector::getDetectorType).thenComparing(Detector::getDetectorName))
                                         .collect(Collectors.toList());

        final List<Detector> buildless = helpJson.getBuildlessDetectors().stream()
                                             .map(Detector::new)
                                             .sorted(Comparator.comparing(Detector::getDetectorType).thenComparing(Detector::getDetectorName))
                                             .collect(Collectors.toList());

        createFromFreemarker(templateProvider, outputDir, "detectors", new DetectorsPage(buildless, build));
    }

    private String encodePropertyLocation(String propertyName) {
        if (!propertyName.equals(propertyName.trim())) {
            throw new RuntimeException("Property name should not include trim-able white space (" + propertyName + ") should be shortened to (" + propertyName.trim() + ") ");
        }
        Map<String, String> supportedCharacters = new HashMap<String, String>();
        String literals = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        for (char literalCharacter : literals.toCharArray()) {
            supportedCharacters.put(String.valueOf(literalCharacter), String.valueOf(literalCharacter));
        }
        supportedCharacters.put(" ", "_");
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
        return encoded.toString();
    }

    private void handleProperties(final TemplateProvider templateProvider, final File outputDir, final HelpJsonData helpJson) throws IntegrationException, IOException, TemplateException {
        final Map<String, String> superGroups = createSuperGroupLookup(helpJson);

        // example: superGroup/key
        final Map<String, String> groupLocations = superGroups.entrySet().stream()
                                                       .collect(Collectors.toMap(Map.Entry::getKey, it -> String.format("%s/%s", it.getValue(), it.getKey()).toLowerCase()));

        // Updating the location on all the json options so that a new object with only 1 new property did not have to be created (and then populated) from the existing.
        for (final HelpJsonOption helpJsonOption : helpJson.getOptions()) {
            final String groupLocation = getGroupLocation(groupLocations, helpJsonOption.getGroup());
            final String encodedPropertyLocation = encodePropertyLocation(helpJsonOption.getPropertyName());
            helpJsonOption.setLocation(String.format("%s/#%s", groupLocation, encodedPropertyLocation)); //ex: superGroup/key/#property_name
        }

        final Map<String, List<HelpJsonOption>> groupedOptions = helpJson.getOptions().stream()
                                                                     .collect(Collectors.groupingBy(HelpJsonOption::getGroup));

        final List<SplitGroup> splitGroupOptions = new ArrayList<>();
        for (final Map.Entry<String, List<HelpJsonOption>> group : groupedOptions.entrySet()) {
            final List<HelpJsonOption> deprecated = group.getValue().stream()
                                                        .filter(HelpJsonOption::getDeprecated)
                                                        .collect(Collectors.toList());

            final List<HelpJsonOption> simple = group.getValue().stream()
                                                    .filter(helpJsonObject -> !deprecated.contains(helpJsonObject) && (StringUtils.isBlank(helpJsonObject.getCategory()) || "simple".equals(helpJsonObject.getCategory())))
                                                    .collect(Collectors.toList());

            final List<HelpJsonOption> advanced = group.getValue().stream()
                                                      .filter(it -> !simple.contains(it) && !deprecated.contains(it))
                                                      .collect(Collectors.toList());

            final String superGroupName = getOrThrow(superGroups, group.getKey(), String.format("Missing super group: %s", group.getKey()));
            final String groupLocation = getGroupLocation(groupLocations, group.getKey());

            // TODO: We only have to do this because we added "(Advanced)" to the section header of advanced properties. Putting property info in the name is not scalable and leads to fixes like this.
            advanced.forEach(property -> property.setLocation(String.format("%s-advanced", property.getLocation())));
            deprecated.forEach(property -> property.setLocation(String.format("%s-deprecated", property.getLocation())));

            final SplitGroup splitGroup = new SplitGroup(group.getKey(), superGroupName, groupLocation, simple, advanced, deprecated);
            splitGroupOptions.add(splitGroup);
        }

        final File propertiesFolder = new File(outputDir, "properties");
        for (final SplitGroup group : splitGroupOptions) {
            final File superGroupFolder = new File(propertiesFolder, group.getSuperGroup().toLowerCase());
            final File targetMarkdown = new File(superGroupFolder, group.getGroupName() + ".md");
            createFromFreemarker(templateProvider, "property-group.ftl", targetMarkdown, group);
        }

        final List<SimplePropertyTableGroup> simplePropertyTableData = new ArrayList<>();
        for (final SplitGroup splitGroupOption : splitGroupOptions) {
            if (splitGroupOption.getSimple().isEmpty()) {
                continue;
            }

            final String groupLocation = getGroupLocation(groupLocations, splitGroupOption.getGroupName());
            final SimplePropertyTableGroup simplePropertyTableGroup = new SimplePropertyTableGroup(splitGroupOption.getGroupName(), groupLocation, splitGroupOption.getSimple());
            simplePropertyTableData.add(simplePropertyTableGroup);
        }

        final List<DeprecatedPropertyTableGroup> deprecatedPropertyTableData = new ArrayList<>();
        for (final SplitGroup splitGroupOption : splitGroupOptions) {
            if (splitGroupOption.getDeprecated().isEmpty()) {
                continue;
            }

            final String groupLocation = getGroupLocation(groupLocations, splitGroupOption.getGroupName());
            final DeprecatedPropertyTableGroup deprecatedPropertyTableGroup = new DeprecatedPropertyTableGroup(splitGroupOption.getGroupName(), groupLocation, splitGroupOption.getDeprecated());
            deprecatedPropertyTableData.add(deprecatedPropertyTableGroup);
        }

        createFromFreemarker(templateProvider, propertiesFolder, "basic-properties", new SimplePropertyTablePage(simplePropertyTableData));
        createFromFreemarker(templateProvider, propertiesFolder, "deprecated-properties", new DeprecatedPropertyTablePage(deprecatedPropertyTableData));
        createFromFreemarker(templateProvider, propertiesFolder, "all-properties", new AdvancedPropertyTablePage(splitGroupOptions));
    }

    private String getGroupLocation(final Map<String, String> groupLocationMap, final String group) throws IntegrationException {
        return getOrThrow(groupLocationMap, group, String.format("Missing group location: %s", group));
    }

    private <K, V> V getOrThrow(final Map<K, V> map, final K key, final String missingMessage) throws IntegrationException {
        return Optional.ofNullable(map.get(key))
                   .orElseThrow(() -> new IntegrationException(missingMessage));
    }

    // Technically each key has exactly 1 super key (but this is not enforced in the json) so here we check that assumption and return the mapping.
    // TODO: Add a new object to the helpJson which is a super key lookup so that the super key lookup is not just embedded in the object and then we don't have to do this at all.
    // TODO: Add the default "Configuration" to the help json instead of blank and having this populate.
    private Map<String, String> createSuperGroupLookup(final HelpJsonData helpJson) {
        final Map<String, String> lookup = new HashMap<>();

        helpJson.getOptions().forEach(option -> {
            final String defaultSuperGroup = "Configuration";
            final String rawSuperGroup = option.getSuperGroup();
            final String superGroup;
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

