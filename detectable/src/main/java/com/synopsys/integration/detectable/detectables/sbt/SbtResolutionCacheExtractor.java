/**
 * detectable
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.sbt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.sbt.model.SbtDependencyModule;
import com.synopsys.integration.detectable.detectables.sbt.model.SbtProject;
import com.synopsys.integration.detectable.detectables.sbt.model.SbtReport;
import com.synopsys.integration.detectable.detectables.sbt.parse.SbtReportParser;
import com.synopsys.integration.util.ExcludedIncludedFilter;

public class SbtResolutionCacheExtractor {
    private final Logger logger = LoggerFactory.getLogger(SbtResolutionCacheExtractor.class);

    private static final String BUILD_SBT_FILENAME = "build.sbt";
    private static final String RESOLUTION_CACHE_DIRECTORY = "resolution-cache";
    private static final String REPORT_DIRECTORY = "reports";
    private static final String REPORT_FILE_DIRECTORY = StringUtils.join(Arrays.asList("", "target", RESOLUTION_CACHE_DIRECTORY, REPORT_DIRECTORY), File.separator);
    private static final String REPORT_FILE_PATTERN = "*.xml";
    private static final String PROJECT_FOLDER = "project";

    private final FileFinder fileFinder;
    private final ExternalIdFactory externalIdFactory;
    private final SbtResolutionCacheDetectableOptions sbtResolutionCacheDetectableOptions;

    public SbtResolutionCacheExtractor(final FileFinder fileFinder, final ExternalIdFactory externalIdFactory,
        final SbtResolutionCacheDetectableOptions sbtResolutionCacheDetectableOptions) {
        this.fileFinder = fileFinder;
        this.externalIdFactory = externalIdFactory;
        this.sbtResolutionCacheDetectableOptions = sbtResolutionCacheDetectableOptions;
    }

    public Extraction extract(final File directory) {
        try {
            final String included = sbtResolutionCacheDetectableOptions.getIncludedConfigurations();
            final String excluded = sbtResolutionCacheDetectableOptions.getExcludedConfigurations();
            final int depth = sbtResolutionCacheDetectableOptions.getReportDepth();

            final SbtProject project = extractProject(directory, depth, included, excluded);

            final List<CodeLocation> codeLocations = new ArrayList<>();

            String projectName = null;
            String projectVersion = null;
            for (final SbtDependencyModule module : project.modules) {
                final CodeLocation codeLocation;
                if (project.projectExternalId != null){
                    codeLocation = new CodeLocation(module.graph, project.projectExternalId);
                } else {
                    codeLocation = new CodeLocation(module.graph);
                }
                if (projectName == null) {
                    projectName = project.projectName;
                    projectVersion = project.projectVersion;
                }
                codeLocations.add(codeLocation);
            }

            if (codeLocations.size() > 0) {
                return new Extraction.Builder().success(codeLocations).projectName(projectName).projectVersion(projectVersion).build();
            } else {
                logger.error("Unable to find any dependency information.");
                return new Extraction.Builder().failure("Unable to find any dependency information.").build();
            }

        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private SbtProject extractProject(final File path, final int depth, final String included, final String excluded) throws IOException, SAXException, ParserConfigurationException {
        final List<SbtDependencyModule> rawModules = extractModules(path, depth, included, excluded);
        final List<SbtDependencyModule> modules = rawModules.stream().filter(it -> it.graph != null).collect(Collectors.toList());
        final int skipped = rawModules.size() - modules.size();
        if (skipped > 0) {
            logger.error(String.format("Skipped %s", skipped));
        }
        final SbtProject result = new SbtProject();
        result.modules = modules;

        if (modules.size() == 0) {
            logger.warn("Unable to create an sbt project, no sbt modules were found.");
        } else if (modules.size() == 1) {
            logger.warn("Found exactly one root module, using it's name and version.");
            result.projectName = modules.get(0).name;
            result.projectVersion = modules.get(0).version;
            result.projectExternalId = externalIdFactory.createMavenExternalId(modules.get(0).org, modules.get(0).name, modules.get(0).version);
        } else {
            logger.warn("Unable to find exactly one root module. Using source path for root project name - will not set an external id.");
            result.projectName = path.getName();
            result.projectVersion = findFirstModuleVersion(modules, result.projectName, "root");
            result.projectExternalId = null;

            if (result.projectVersion == null && modules.size() > 1) {
                logger.warn(String.format("Getting version from first project: %s", modules.get(0).name));
                result.projectVersion = modules.get(0).version;
            }
        }

        return result;
    }

    private String findFirstModuleVersion(final List<SbtDependencyModule> modules, final String... names) {
        String version = null;
        final List<String> nameList = new ArrayList<>(Arrays.asList(names));

        for (final SbtDependencyModule it : modules) {
            if (version == null && it.name != null && nameList.contains(it.name)) {
                logger.debug(String.format("Matched %s to project version.", it.name));
                version = it.version;
            }
        }
        return version;
    }

    private List<SbtDependencyModule> extractModules(final File path, final int depth, final String included, final String excluded) throws IOException, SAXException, ParserConfigurationException {
        final List<File> sbtFiles = fileFinder.findFiles(path, BUILD_SBT_FILENAME, depth);
        final List<File> resolutionCaches = fileFinder.findFiles(path, RESOLUTION_CACHE_DIRECTORY, depth); // TODO: ensure this does what the old method did. findDirectoriesContainingDirectoriesToDepth

        logger.info(String.format("Found %s build.sbt files.", sbtFiles.size()));
        logger.info(String.format("Found %s resolution caches.", resolutionCaches.size()));

        final List<SbtDependencyModule> modules = new ArrayList<>();
        final List<String> usedReports = new ArrayList<>();

        for (final File sbtFile : sbtFiles) {
            logger.debug(String.format("Found SBT build file: %s", sbtFile.getCanonicalPath()));
            final File sbtDirectory = sbtFile.getParentFile();
            final File reportPath = new File(sbtDirectory, REPORT_FILE_DIRECTORY);

            final List<SbtDependencyModule> foundModules = extractReportModules(path, reportPath, sbtDirectory, included, excluded, usedReports);
            modules.addAll(foundModules);
        }

        for (final File resCache : resolutionCaches) {
            logger.debug(String.format("Found resolution cache: %s", resCache.getCanonicalPath()));
            final File reportPath = new File(resCache, REPORT_DIRECTORY);
            final List<SbtDependencyModule> foundModules = extractReportModules(path, reportPath, resCache.getParentFile(), included, excluded, usedReports);
            modules.addAll(foundModules);
        }

        modules.removeIf(it -> {
            if (it.name.contains("temp-module")) {
                logger.info("Excluding temp module: " + it.name);
                return true;
            } else {
                return false;
            }
        });

        if (modules.size() == 0) {
            if (sbtFiles.size() == 0) {
                logger.error("Sbt found no build.sbt files even though it applied.");
            } else if (resolutionCaches.size() == 0) {
                logger.error("Sbt found no resolution-caches, this most likely means you are not running post build.");
                logger.error("Please build the project before running detect.");
            } else {
                logger.error("Sbt was unable to parse any dependencies from any resolution caches.");
            }
        }

        return modules;
    }

    private Boolean isInProject(final File file, final File sourcePath) throws IOException {
        final File projectPath = new File(sourcePath, PROJECT_FOLDER);
        return file.getCanonicalPath().startsWith(projectPath.getCanonicalPath());
    }

    private List<SbtDependencyModule> extractReportModules(final File path, final File reportPath, final File source, final String included, final String excluded, final List<String> usedReports)
        throws IOException, SAXException, ParserConfigurationException {
        final List<SbtDependencyModule> modules = new ArrayList<>();
        final String canonical = reportPath.getCanonicalPath();
        if (usedReports.contains(canonical)) {
            logger.debug(String.format("Skipping already processed report folder: %s", canonical));
        } else if (isInProject(reportPath, path)) {
            logger.debug(String.format("Skipping reports in project folder: %s", reportPath.getCanonicalPath()));
        } else {
            usedReports.add(canonical);
            final List<File> reportFiles = fileFinder.findFiles(reportPath, REPORT_FILE_PATTERN);
            if (reportFiles == null || reportFiles.size() <= 0) {
                logger.debug(String.format("No reports were found in: %s", reportPath));
            } else {
                final List<SbtDependencyModule> aggregatedModules = makeModuleAggregate(reportFiles, included, excluded);

                if (aggregatedModules == null) {
                    logger.debug(String.format("No dependencies were generated for report folder: %s", reportPath));
                } else {
                    logger.debug(String.format("Found %s aggregate dependencies in report folder: %s", aggregatedModules.size(), reportPath));
                    for (final SbtDependencyModule aggregatedModule : aggregatedModules) {
                        logger.debug(String.format("Generated root node of %s %s", aggregatedModule.name, aggregatedModule.version));

                        aggregatedModule.sourcePath = source;

                        modules.add(aggregatedModule);
                    }

                }
            }
        }
        return modules;
    }

    private List<SbtDependencyModule> makeModuleAggregate(final List<File> reportFiles, final String include, final String exclude) throws SAXException, IOException, ParserConfigurationException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();

        final SbtReportParser parser = new SbtReportParser();
        final SbtDependencyResolver resolver = new SbtDependencyResolver(externalIdFactory);
        final ExcludedIncludedFilter filter = new ExcludedIncludedFilter(exclude, include);
        final SbtModuleAggregator aggregator = new SbtModuleAggregator();

        final List<SbtDependencyModule> modules = new ArrayList<>();
        for (final File reportFile : reportFiles) {
            final Document xml = builder.parse(reportFile);
            logger.debug(String.format("Parsing SBT report file: %s", reportFile.getCanonicalPath()));
            final SbtReport report = parser.parseReportFromXml(xml);
            final SbtDependencyModule tree = resolver.resolveReport(report);
            modules.add(tree);
        }

        final List<SbtDependencyModule> includedModules = modules.stream().filter(module -> filter.shouldInclude(module.configuration)).collect(Collectors.toList());

        if (modules.size() <= 0) {
            logger.warn("No sbt configurations were found in report folder.");
            return null;
        } else if (includedModules.size() <= 0) {
            logger.warn(String.format("Although %s configs were found, none were included.", modules.size()));
            return null;
        }

        return aggregator.aggregateModules(includedModules);
    }
}
