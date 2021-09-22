/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.sbt.parse;

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

import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.sbt.parse.model.SbtDependencyModule;
import com.synopsys.integration.detectable.detectables.sbt.parse.model.SbtProject;
import com.synopsys.integration.detectable.detectables.sbt.parse.model.SbtReport;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.util.ExcludedIncludedWildcardFilter;

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

    public SbtResolutionCacheExtractor(FileFinder fileFinder, ExternalIdFactory externalIdFactory) {
        this.fileFinder = fileFinder;
        this.externalIdFactory = externalIdFactory;
    }

    public Extraction extract(File directory, SbtResolutionCacheOptions sbtResolutionCacheOptions) { //TODO: Extractor should not use DetectableOptions
        try {
            // TODO: Handle null better.
            List<String> included = sbtResolutionCacheOptions.getIncludedConfigurations();
            List<String> excluded = sbtResolutionCacheOptions.getExcludedConfigurations();
            int depth = sbtResolutionCacheOptions.getReportDepth();

            SbtProject project = extractProject(directory, sbtResolutionCacheOptions.isFollowSymLinks(), depth, included, excluded);

            List<CodeLocation> codeLocations = new ArrayList<>();

            String projectName = null;
            String projectVersion = null;
            for (SbtDependencyModule module : project.getModules()) {
                CodeLocation codeLocation;
                if (project.getProjectExternalId() != null) {
                    codeLocation = new CodeLocation(module.getGraph(), project.getProjectExternalId());
                } else {
                    codeLocation = new CodeLocation(module.getGraph());
                }
                if (projectName == null) {
                    projectName = project.getProjectName();
                    projectVersion = project.getProjectVersion();
                }
                codeLocations.add(codeLocation);
            }

            if (codeLocations.size() > 0) {
                return new Extraction.Builder().success(codeLocations).projectName(projectName).projectVersion(projectVersion).build();
            } else {
                logger.error("Unable to find any dependency information.");
                return new Extraction.Builder().failure("Unable to find any dependency information.").build();
            }

        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private SbtProject extractProject(File path, boolean followSymLinks, int depth, List<String> included, List<String> excluded) throws IOException, SAXException, ParserConfigurationException {
        List<SbtDependencyModule> rawModules = extractModules(path, followSymLinks, depth, included, excluded);
        List<SbtDependencyModule> modules = rawModules.stream().filter(it -> it.getGraph() != null).collect(Collectors.toList());
        int skipped = rawModules.size() - modules.size();
        if (skipped > 0) {
            logger.error(String.format("Skipped %s", skipped));
        }
        SbtProject result = new SbtProject();
        result.setModules(modules);

        if (modules.isEmpty()) {
            logger.warn("Unable to create an sbt project, no sbt modules were found.");
        } else if (modules.size() == 1) {
            logger.warn("Found exactly one root module, using it's name and version.");
            result.setProjectName(modules.get(0).getName());
            result.setProjectVersion(modules.get(0).getVersion());
            result.setProjectExternalId(externalIdFactory.createMavenExternalId(modules.get(0).getOrg(), modules.get(0).getName(), modules.get(0).getVersion()));
        } else {
            logger.warn("Unable to find exactly one root module. Using source path for root project name - will not set an external id.");
            result.setProjectName(path.getName());
            result.setProjectVersion(findFirstModuleVersion(modules, result.getProjectName(), "root"));
            result.setProjectExternalId(null);

            if (result.getProjectVersion() == null && modules.size() > 1) {
                logger.warn(String.format("Getting version from first project: %s", modules.get(0).getName()));
                result.setProjectVersion(modules.get(0).getVersion());
            }
        }

        return result;
    }

    private String findFirstModuleVersion(List<SbtDependencyModule> modules, String... names) {
        String version = null;
        List<String> nameList = new ArrayList<>(Arrays.asList(names));

        for (SbtDependencyModule it : modules) {
            if (version == null && it.getName() != null && nameList.contains(it.getName())) {
                logger.debug(String.format("Matched %s to project version.", it.getName()));
                version = it.getVersion();
            }
        }
        return version;
    }

    private List<SbtDependencyModule> extractModules(File path, boolean followSymLinks, int depth, List<String> included, List<String> excluded) throws IOException, SAXException, ParserConfigurationException {
        List<File> sbtFiles = fileFinder.findFiles(path, BUILD_SBT_FILENAME, followSymLinks, depth);
        List<File> resolutionCaches = fileFinder.findFiles(path, RESOLUTION_CACHE_DIRECTORY, followSymLinks, depth); // TODO: ensure this does what the old method did. findDirectoriesContainingDirectoriesToDepth

        logger.debug(String.format("Found %s build.sbt files.", sbtFiles.size()));
        logger.debug(String.format("Found %s resolution caches.", resolutionCaches.size()));

        List<SbtDependencyModule> modules = new ArrayList<>();
        List<String> usedReports = new ArrayList<>();

        for (File sbtFile : sbtFiles) {
            logger.debug(String.format("Found SBT build file: %s", sbtFile.getCanonicalPath()));
            File sbtDirectory = sbtFile.getParentFile();
            File reportPath = new File(sbtDirectory, REPORT_FILE_DIRECTORY);

            List<SbtDependencyModule> foundModules = extractReportModules(path, reportPath, sbtDirectory, included, excluded, usedReports);
            modules.addAll(foundModules);
        }

        for (File resCache : resolutionCaches) {
            logger.debug(String.format("Found resolution cache: %s", resCache.getCanonicalPath()));
            File reportPath = new File(resCache, REPORT_DIRECTORY);
            List<SbtDependencyModule> foundModules = extractReportModules(path, reportPath, resCache.getParentFile(), included, excluded, usedReports);
            modules.addAll(foundModules);
        }

        modules.removeIf(it -> {
            if (it.getName().contains("temp-module")) {
                logger.debug("Excluding temp module: " + it.getName());
                return true;
            } else {
                return false;
            }
        });

        if (modules.isEmpty()) {
            if (sbtFiles.isEmpty()) {
                logger.error("Sbt found no build.sbt files even though it applied.");
            } else if (resolutionCaches.isEmpty()) {
                logger.error("Sbt found no resolution-caches, this most likely means you are not running post build.");
                logger.error("Please build the project before running detect.");
            } else {
                logger.error("Sbt was unable to parse any dependencies from any resolution caches.");
            }
        }

        return modules;
    }

    private Boolean isInProject(File file, File sourcePath) throws IOException {
        File projectPath = new File(sourcePath, PROJECT_FOLDER);
        return file.getCanonicalPath().startsWith(projectPath.getCanonicalPath());
    }

    private List<SbtDependencyModule> extractReportModules(File path, File reportPath, File source, List<String> included, List<String> excluded, List<String> usedReports)
        throws IOException, SAXException, ParserConfigurationException {
        List<SbtDependencyModule> modules = new ArrayList<>();
        String canonical = reportPath.getCanonicalPath();
        if (usedReports.contains(canonical)) {
            logger.debug(String.format("Skipping already processed report folder: %s", canonical));
        } else if (isInProject(reportPath, path)) {
            logger.debug(String.format("Skipping reports in project folder: %s", reportPath.getCanonicalPath()));
        } else {
            usedReports.add(canonical);
            List<File> reportFiles = fileFinder.findFiles(reportPath, REPORT_FILE_PATTERN);
            if (reportFiles == null || reportFiles.isEmpty()) {
                logger.debug(String.format("No reports were found in: %s", reportPath));
            } else {
                List<SbtDependencyModule> aggregatedModules = makeModuleAggregate(reportFiles, included, excluded);

                if (aggregatedModules == null) {
                    logger.debug(String.format("No dependencies were generated for report folder: %s", reportPath));
                } else {
                    logger.debug(String.format("Found %s aggregate dependencies in report folder: %s", aggregatedModules.size(), reportPath));
                    for (SbtDependencyModule aggregatedModule : aggregatedModules) {
                        logger.debug(String.format("Generated root node of %s %s", aggregatedModule.getName(), aggregatedModule.getVersion()));

                        aggregatedModule.setSourcePath(source);

                        modules.add(aggregatedModule);
                    }

                }
            }
        }
        return modules;
    }

    private List<SbtDependencyModule> makeModuleAggregate(List<File> reportFiles, List<String> include, List<String> exclude) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        SbtReportParser parser = new SbtReportParser();
        SbtDependencyResolver resolver = new SbtDependencyResolver(externalIdFactory);
        ExcludedIncludedWildcardFilter filter = ExcludedIncludedWildcardFilter.fromCollections(exclude, include);
        SbtModuleAggregator aggregator = new SbtModuleAggregator();

        List<SbtDependencyModule> modules = new ArrayList<>();
        for (File reportFile : reportFiles) {
            Document xml = builder.parse(reportFile);
            logger.debug(String.format("Parsing SBT report file: %s", reportFile.getCanonicalPath()));
            SbtReport report = parser.parseReportFromXml(xml);
            SbtDependencyModule tree = resolver.resolveReport(report);
            modules.add(tree);
        }

        List<SbtDependencyModule> includedModules = modules.stream().filter(module -> filter.shouldInclude(module.getConfiguration())).collect(Collectors.toList());

        if (modules.isEmpty()) {
            logger.warn("No sbt configurations were found in report folder.");
            return null;
        } else if (includedModules.isEmpty()) {
            logger.warn(String.format("Although %s configs were found, none were included.", modules.size()));
            return null;
        }

        return aggregator.aggregateModules(includedModules);
    }
}
