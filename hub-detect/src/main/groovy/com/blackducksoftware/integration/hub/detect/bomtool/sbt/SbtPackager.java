/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.bomtool.sbt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.util.ExcludedIncludedFilter;

public class SbtPackager {
    private final Logger logger = LoggerFactory.getLogger(SbtPackager.class);

    private static final String BUILD_SBT_FILENAME = "build.sbt";
    private static final String REPORT_FILE_DIRECTORY = File.separator + "target" + File.separator + "resolution-cache" + File.separator + "reports";
    private static final String REPORT_SEARCH_PATTERN = "resolution-cache";
    private static final String REPORT_DIRECTORY = "reports";
    private static final String REPORT_FILE_PATTERN = "*.xml";
    private static final String PROJECT_FOLDER = "project";

    private final ExternalIdFactory externalIdFactory;
    private final DetectFileFinder detectFileFinder;

    public SbtPackager(final ExternalIdFactory externalIdFactory, final DetectFileFinder detectFileFinder) {
        this.externalIdFactory = externalIdFactory;
        this.detectFileFinder = detectFileFinder;
    }

    public List<SbtDependencyModule> makeModuleAggregate(final List<File> reportFiles, final String include, final String exclude) throws SAXException, IOException, ParserConfigurationException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();

        final SbtReportParser parser = new SbtReportParser();
        final SbtDependencyResolver resolver = new SbtDependencyResolver(externalIdFactory);
        final ExcludedIncludedFilter filter = new ExcludedIncludedFilter(exclude, include);
        final SbtModuleAggregator aggregator = new SbtModuleAggregator();

        final List<SbtDependencyModule> modules = new ArrayList<>();
        for (final File reportFile : reportFiles) {
            final Document xml = builder.parse(reportFile);
            logger.debug("Parsing SBT report file : " + reportFile.getCanonicalPath());
            final SbtReport report = parser.parseReportFromXml(xml);
            final SbtDependencyModule tree = resolver.resolveReport(report);
            modules.add(tree);
        }

        final List<SbtDependencyModule> includedModules = modules.stream().filter(module -> filter.shouldInclude(module.configuration)).collect(Collectors.toList());

        if (modules.size() <= 0) {
            logger.warn("No sbt configurations were found in report folder.");
            return null;
        } else if (includedModules.size() <= 0) {
            logger.warn("Although " + modules.size() + " configs were found, none were included.");
            return null;
        }

        return aggregator.aggregateModules(includedModules);
    }

    public SbtProject extractProject(final String path, final int depth, final String included, final String excluded) throws IOException, SAXException, ParserConfigurationException {
        final List<SbtDependencyModule> rawModules = extractModules(path, depth, included, excluded);
        final List<SbtDependencyModule> modules = rawModules.stream().filter(it -> it.graph != null).collect(Collectors.toList());
        final int skipped = rawModules.size() - modules.size();
        if (skipped > 0) {
            logger.error("Skipped " + skipped);
        }
        final SbtProject result = new SbtProject();
        result.bomToolType = BomToolGroupType.SBT;
        result.modules = modules;

        if (modules.size() == 0) {
            logger.warn("Unable to create an sbt project, no sbt modules were found.");
        } else if (modules.size() == 1) {
            logger.warn("Found exactly one root module, using it's name and version.");
            result.projectName = modules.get(0).name;
            result.projectVersion = modules.get(0).version;
            result.projectExternalId = externalIdFactory.createMavenExternalId(modules.get(0).org, modules.get(0).name, modules.get(0).version);
        } else {
            logger.warn("Unable to find exactly one root module. Using source path for root project name.");
            result.projectName = detectFileFinder.extractFinalPieceFromPath(path);
            result.projectVersion = findFirstModuleVersion(modules, result.projectName, "root");
            result.projectExternalId = externalIdFactory.createPathExternalId(Forge.MAVEN, path);

            if (result.projectVersion == null && modules.size() > 1) {
                logger.warn("Getting version from first project: " + modules.get(0).name);
                result.projectVersion = modules.get(0).version;
            }
        }

        return result;
    }

    private String findFirstModuleVersion(final List<SbtDependencyModule> modules, final String... names) {
        String version = null;
        final List<String> nameList = new ArrayList<>();
        for (final String name : names) {
            nameList.add(name);
        }
        for (final SbtDependencyModule it : modules) {
            if (version == null && it.name != null && nameList.contains(it.name)) {
                logger.debug(String.format("Matched %s to project version.", it.name));
                version = it.version;
            }
        }
        return version;
    }

    private List<SbtDependencyModule> extractModules(final String path, final int depth, final String included, final String excluded) throws IOException, SAXException, ParserConfigurationException {
        final List<File> sbtFiles = detectFileFinder.findFilesToDepth(path, BUILD_SBT_FILENAME, depth);
        final List<File> resolutionCaches = detectFileFinder.findDirectoriesContainingDirectoriesToDepth(path, REPORT_SEARCH_PATTERN, depth);

        logger.info(String.format("Found %s build.sbt files.", sbtFiles.size()));
        logger.info(String.format("Found %s resolution caches.", resolutionCaches.size()));

        final List<SbtDependencyModule> modules = new ArrayList<>();
        final List<String> usedReports = new ArrayList<>();

        for (final File sbtFile : sbtFiles) {
            logger.debug("Found SBT build file : " + sbtFile.getCanonicalPath());
            final File sbtDirectory = sbtFile.getParentFile();
            final File reportPath = new File(sbtDirectory, REPORT_FILE_DIRECTORY);

            final List<SbtDependencyModule> foundModules = extractReportModules(path, reportPath, sbtDirectory, included, excluded, usedReports);
            modules.addAll(foundModules);
        }

        for (final File resCache : resolutionCaches) {
            logger.debug("Found resolution cache : " + resCache.getCanonicalPath());
            final File reportPath = new File(resCache, REPORT_DIRECTORY);
            final List<SbtDependencyModule> foundModules = extractReportModules(path, reportPath, resCache.getParentFile(), included, excluded, usedReports);
            modules.addAll(foundModules);
        }

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

    private Boolean isInProject(final File file, final String sourcePath) throws IOException {
        final File projectPath = new File(sourcePath, PROJECT_FOLDER);
        return file.getCanonicalPath().startsWith(projectPath.getCanonicalPath());
    }

    private List<SbtDependencyModule> extractReportModules(final String path, final File reportPath, final File source, final String included, final String excluded, final List<String> usedReports)
            throws IOException, SAXException, ParserConfigurationException {
        final List<SbtDependencyModule> modules = new ArrayList<>();
        final String canonical = reportPath.getCanonicalPath();
        if (usedReports.contains(canonical)) {
            logger.debug("Skipping already processed report folder: " + canonical);
        } else if (isInProject(reportPath, path)) {
            logger.debug("Skipping reports in project folder: " + reportPath.getCanonicalPath());
        } else {
            usedReports.add(canonical);
            final List<File> reportFiles = detectFileFinder.findFiles(reportPath, REPORT_FILE_PATTERN);
            if (reportFiles == null || reportFiles.size() <= 0) {
                logger.debug("No reports were found in: " + reportPath);
            } else {
                final List<SbtDependencyModule> aggregatedModules = makeModuleAggregate(reportFiles, included, excluded);

                if (aggregatedModules == null) {
                    logger.debug("No dependencies were generated for report folder: " + reportPath);
                } else {
                    logger.debug(String.format("Found %s aggregate dependencies in report folder: %s", aggregatedModules.size(), reportPath));
                    for (final SbtDependencyModule aggregatedModule : aggregatedModules) {
                        logger.debug(String.format("Generated root node of %s %s ", aggregatedModule.name, aggregatedModule.version));

                        aggregatedModule.sourcePath = source.getCanonicalPath();

                        modules.add(aggregatedModule);
                    }

                }
            }
        }
        return modules;
    }
}
