/*
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
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
package com.blackducksoftware.integration.hub.detect.bomtool.gradle;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Stack;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.model.DetectProject;

@Component
public class GradleDependenciesParser {
    private final Logger logger = LoggerFactory.getLogger(GradleDependenciesParser.class);

    private static final String[] DEPENDENCY_INDICATORS = new String[] { "+---", "\\---" };
    private static final String[] PROJECT_INDICATORS = new String[] { "+--- project :", "\\--- project :" };

    private static final String COMPONENT_PREFIX = "--- ";
    private static final String SEEN_ELSEWHERE_SUFFIX = " (*)";
    private static final String WINNING_INDICATOR = " -> ";

    private String rootProjectName = "";
    private String rootProjectVersionName = "";
    private String projectSourcePath = "";
    private String projectGroup = "";
    private String projectName = "";
    private String projectVersionName = "";
    private boolean processingMetaData = false;
    private boolean processingBeneathProject = false;
    private boolean processingConfiguration = false;
    private String configurationName = null;
    private String previousLine = null;
    private MutableDependencyGraph graph = new MutableMapDependencyGraph();
    private Stack<Dependency> nodeStack = new Stack<>();
    private Dependency previousNode = null;
    private int treeLevel = 0;

    @Autowired
    private ExternalIdFactory externalIdFactory;

    public DetectCodeLocation parseDependencies(final DetectProject detectProject, final InputStream dependenciesInputStream) {
        clearState();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(dependenciesInputStream, StandardCharsets.UTF_8));
            while (reader.ready()) {
                final String line = reader.readLine();
                /**
                 * The meta data section will be at the end of the file after all of the "gradle dependencies" output
                 */
                if (line.startsWith("DETECT META DATA START")) {
                    processingMetaData = true;
                    continue;
                }
                if (line.startsWith("DETECT META DATA END")) {
                    processingMetaData = false;
                    continue;
                }
                if (processingMetaData) {
                    processMetaDataLine(line);
                    continue;
                }

                if (StringUtils.isBlank(line)) {
                    clearConfigurationState();
                    continue;
                }

                if (!processingConfiguration) {
                    if (isRootLevel(line)) {
                        processingConfiguration = true;
                        configurationName = extractConfigurationName(previousLine);
                        logger.info(String.format("processing of configuration %s started", configurationName));
                    } else {
                        previousLine = line;
                        continue;
                    }
                }

                if (processingBeneathProject && !isRootLevel(line)) {
                    previousLine = line;
                    continue;
                } else if (isRootLevelProject(line)) {
                    processingBeneathProject = true;
                    continue;
                } else {
                    processingBeneathProject = false;
                }

                if (StringUtils.isBlank(line) || !line.contains(COMPONENT_PREFIX)) {
                    continue;
                }

                final Dependency lineNode = createDependencyNodeFromOutputLine(line);
                if (lineNode == null) {
                    previousLine = line;
                    continue;
                }

                final int lineTreeLevel = getLineLevel(line);
                if (lineTreeLevel == treeLevel + 1) {
                    nodeStack.push(previousNode);
                } else if (lineTreeLevel < treeLevel) {
                    for (int times = 0; times < (treeLevel - lineTreeLevel); times++) {
                        nodeStack.pop();
                    }
                } else if (lineTreeLevel != treeLevel) {
                    logger.error(String.format("The tree level (%s) and this line (%s) with count %s can't be reconciled.", treeLevel, line, lineTreeLevel));
                }
                if (nodeStack.size() == 0) {
                    graph.addChildToRoot(lineNode);
                } else {
                    graph.addChildWithParents(lineNode, nodeStack.peek());
                }
                previousNode = lineNode;
                treeLevel = lineTreeLevel;
                previousLine = line;
            }
        } catch (final Exception e) {
            logger.error("Exception parsing gradle output: " + e.getMessage());
        } finally {
            IOUtils.closeQuietly(reader);
        }
        detectProject.setProjectNameIfNotSet(rootProjectName);
        detectProject.setProjectVersionNameIfNotSet(rootProjectVersionName);

        final ExternalId id = externalIdFactory.createMavenExternalId(projectGroup, projectName, projectVersionName);
        final DetectCodeLocation detectCodeLocation = new DetectCodeLocation(BomToolType.GRADLE, projectSourcePath, projectName, projectVersionName, id, graph);
        return detectCodeLocation;
    }

    private void clearState() {
        rootProjectName = "";
        rootProjectVersionName = "";
        projectSourcePath = "";
        projectGroup = "";
        projectName = "";
        projectVersionName = "";
        processingMetaData = false;
        processingConfiguration = false;
        configurationName = null;
        previousLine = null;
        graph = new MutableMapDependencyGraph();
        nodeStack = new Stack<>();
        previousNode = null;
        treeLevel = 0;
    }

    private void clearConfigurationState() {
        processingBeneathProject = false;
        processingConfiguration = false;
        configurationName = null;
        previousLine = null;
        nodeStack = new Stack<>();
        previousNode = null;
        treeLevel = 0;
    }

    private String extractConfigurationName(final String nameLine) {
        if (nameLine.contains(" - ")) {
            return nameLine.substring(0, nameLine.indexOf(" - ")).trim();
        } else {
            return nameLine.trim();
        }
    }

    private boolean startsWithAny(final String thing, final String[] targets) {
        boolean startsWith = false;
        for (final String target : targets) {
            startsWith = startsWith || thing.startsWith(target);
        }
        return startsWith;
    }

    private String removeDependencyIndicators(final String line) {
        int indexToCut = line.length();
        for (final String target : DEPENDENCY_INDICATORS) {
            if (line.contains(target)) {
                indexToCut = line.indexOf(target);
            }
        }

        return line.substring(0, indexToCut);

    }

    public int getLineLevel(final String line) {
        if (isRootLevel(line)) {
            return 0;
        }

        String modifiedLine = removeDependencyIndicators(line);

        if (!modifiedLine.startsWith("|")) {
            modifiedLine = "|" + modifiedLine;
        }
        modifiedLine = modifiedLine.replace("     ", "    |");
        modifiedLine = modifiedLine.replace("||", "|");
        if (modifiedLine.endsWith("|")) {
            modifiedLine = modifiedLine.substring(0, modifiedLine.length() - 5);
        }
        final int matches = StringUtils.countMatches(modifiedLine, "|");

        return matches;
    }

    public Dependency createDependencyNodeFromOutputLine(final String outputLine) {
        String cleanedOutput = StringUtils.trimToEmpty(outputLine);
        cleanedOutput = cleanedOutput.substring(cleanedOutput.indexOf(COMPONENT_PREFIX) + COMPONENT_PREFIX.length());
        if (cleanedOutput.endsWith(SEEN_ELSEWHERE_SUFFIX)) {
            final int lastSeenElsewhereIndex = cleanedOutput.lastIndexOf(SEEN_ELSEWHERE_SUFFIX);
            cleanedOutput = cleanedOutput.substring(0, lastSeenElsewhereIndex);
        }

        String[] gav = cleanedOutput.split(":");
        if (cleanedOutput.contains(WINNING_INDICATOR)) {
            // WINNING_INDICATOR can point to an entire GAV not just a version
            final String winningSection = cleanedOutput.substring(cleanedOutput.indexOf(WINNING_INDICATOR) + WINNING_INDICATOR.length());
            if (winningSection.contains(":")) {
                gav = winningSection.split(":");
            } else {
                gav[2] = winningSection;
            }
        }
        if (gav.length != 3) {
            logger.error(String.format("The line can not be reasonably split in to the neccessary parts: %s", outputLine));
            return null;
        }
        final String group = gav[0];
        final String artifact = gav[1];
        final String version = gav[2];

        final Dependency dependency = new Dependency(artifact, version, externalIdFactory.createMavenExternalId(group, artifact, version));
        return dependency;
    }

    private void processMetaDataLine(final String metaDataLine) {
        if (metaDataLine.startsWith("rootProjectName:")) {
            rootProjectName = metaDataLine.substring("rootProjectName:".length()).trim();
        } else if (metaDataLine.startsWith("rootProjectVersion:")) {
            rootProjectVersionName = metaDataLine.substring("rootProjectVersion:".length()).trim();
        } else if (metaDataLine.startsWith("projectPath:")) {
            projectSourcePath = metaDataLine.substring("projectPath:".length()).trim();
        } else if (metaDataLine.startsWith("projectGroup:")) {
            projectGroup = metaDataLine.substring("projectGroup:".length()).trim();
        } else if (metaDataLine.startsWith("projectName:")) {
            projectName = metaDataLine.substring("projectName:".length()).trim();
        } else if (metaDataLine.startsWith("projectVersion:")) {
            projectVersionName = metaDataLine.substring("projectVersion:".length()).trim();
        }
    }

    private boolean isRootLevel(final String line) {
        return isRootLevelProject(line) || isRootLevelDependency(line);
    }

    private boolean isRootLevelDependency(final String line) {
        return startsWithAny(line, DEPENDENCY_INDICATORS);
    }

    private boolean isRootLevelProject(final String line) {
        return startsWithAny(line, PROJECT_INDICATORS);
    }

}
