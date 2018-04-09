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
package com.blackducksoftware.integration.hub.detect.bomtool.maven;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.util.ExcludedIncludedFilter;

@Component
class MavenCodeLocationPackager {
    public static final List<String> indentationStrings = Arrays.asList("+- ", "|  ", "\\- ", "   ");
    private static final Logger logger = LoggerFactory.getLogger(MavenCodeLocationPackager.class);

    private final ExternalIdFactory externalIdFactory;
    private List<DetectCodeLocation> codeLocations = new ArrayList<>();
    private DetectCodeLocation currentCodeLocation = null;
    private Stack<Dependency> dependencyParentStack = new Stack<>();
    private boolean parsingProjectSection;
    private int level;
    private MutableDependencyGraph currentGraph = null;

    public MavenCodeLocationPackager(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public List<DetectCodeLocation> extractCodeLocations(String sourcePath, String mavenOutputText, String excludedModules, String includedModules) {
        ExcludedIncludedFilter filter = new ExcludedIncludedFilter(excludedModules, includedModules);
        codeLocations = new ArrayList<>();
        currentCodeLocation = null;
        dependencyParentStack = new Stack<>();
        parsingProjectSection = false;
        currentGraph = new MutableMapDependencyGraph();

        level = 0;
        for (String currentLine : mavenOutputText.split(System.lineSeparator())) {
            String line = currentLine.trim();
            if (!isLineRelevant(line)) {
                continue;
            }
            line = trimLogLevel(line);
            if (StringUtils.isBlank(line)) {
                continue;
            }
            if (isProjectSection(line)) {
                parsingProjectSection = true;
                continue;
            }

            if (!parsingProjectSection) {
                continue;
            }

            if (parsingProjectSection && currentCodeLocation == null) {
                //this is the first line of a new code location, the following lines will be the tree of dependencies for this code location
                currentGraph = new MutableMapDependencyGraph();
                DetectCodeLocation detectCodeLocation = createNewCodeLocation(sourcePath, line, currentGraph);
                if (null != detectCodeLocation && filter.shouldInclude(detectCodeLocation.getBomToolProjectName())) {
                    this.currentCodeLocation = detectCodeLocation;
                    codeLocations.add(detectCodeLocation);
                } else {
                    currentCodeLocation = null;
                    dependencyParentStack.clear();
                    parsingProjectSection = false;
                    level = 0;
                }
                continue;
            }

            final boolean finished = line.contains("--------");
            if (finished) {
                currentCodeLocation = null;
                dependencyParentStack.clear();
                parsingProjectSection = false;
                level = 0;
                continue;
            }

            int previousLevel = level;
            String cleanedLine = calculateCurrentLevelAndCleanLine(line);
            Dependency dependency = textToDependency(cleanedLine);
            if (null == dependency) {
                continue;
            }

            if (currentCodeLocation != null) {
                if (level == 1) {
                    //a direct dependency, clear the stack and add this as a potential parent for the next line
                    currentGraph.addChildToRoot(dependency);
                    dependencyParentStack.clear();
                    dependencyParentStack.push(dependency);
                } else {
                    //level should be greater than 1
                    if (level == previousLevel) {
                        //a sibling of the previous dependency
                        dependencyParentStack.pop();
                        currentGraph.addParentWithChild(dependencyParentStack.peek(), dependency);
                        dependencyParentStack.push(dependency);
                    } else if (level > previousLevel) {
                        //a child of the previous dependency
                        currentGraph.addParentWithChild(dependencyParentStack.peek(), dependency);
                        dependencyParentStack.push(dependency);
                    } else {
                        //a child of a dependency further back than 1 line
                        for (int i = previousLevel; i >= level; i--) {
                            dependencyParentStack.pop();
                        }
                        currentGraph.addParentWithChild(dependencyParentStack.peek(), dependency);
                        dependencyParentStack.push(dependency);
                    }
                }
            }
        }

        return codeLocations;
    }

    private DetectCodeLocation createNewCodeLocation(String sourcePath, String line, DependencyGraph graph) {
        Dependency dependency = textToProject(line);
        if (null != dependency) {
            String codeLocationSourcePath = sourcePath;
            if (!sourcePath.endsWith(dependency.name)) {
                codeLocationSourcePath += "/" + dependency.name;
            }
            return new DetectCodeLocation.Builder(BomToolType.MAVEN, codeLocationSourcePath, dependency.externalId, graph).bomToolProjectName(dependency.name).bomToolProjectVersionName(dependency.version).build();
        }
        return null;
    }

    private String calculateCurrentLevelAndCleanLine(String line) {
        level = 0;
        String cleanedLine = line;
        for (String pattern : indentationStrings) {
            while (cleanedLine.contains(pattern)) {
                level++;
                cleanedLine = cleanedLine.replaceFirst(Pattern.quote(pattern), "");
            }
        }

        return cleanedLine;
    }

    private Dependency textToDependency(final String componentText) {
        if (!isGav(componentText)) {
            return null;
        }
        String[] gavParts = componentText.split(":");
        String group = gavParts[0];
        String artifact = gavParts[1];

        String scope = gavParts[gavParts.length - 1];
        if (!(scope.equalsIgnoreCase("compile") || scope.equalsIgnoreCase("provided") || scope.equalsIgnoreCase("runtime") || scope.equalsIgnoreCase("test") || scope.equalsIgnoreCase("system") || scope.equalsIgnoreCase("import"))) {
            logger.debug("This dependency is invalid, if does not end in a Maven scope that we recognize");
            return null;
        }
        String version = gavParts[gavParts.length - 2];
        ExternalId externalId = externalIdFactory.createMavenExternalId(group, artifact, version);
        return new Dependency(artifact, version, externalId);
    }

    private Dependency textToProject(final String componentText) {
        if (!isGav(componentText)) {
            return null;
        }
        String[] gavParts = componentText.split(":");
        String group = gavParts[0];
        String artifact = gavParts[1];
        String version;
        if (gavParts.length == 4) {
            // Dependency does not include the classifier
            version = gavParts[gavParts.length - 1];
        } else if (gavParts.length == 5) {
            // Dependency does include the classifier
            version = gavParts[gavParts.length - 1];
        } else {
            logger.debug(String.format("%s does not look like a dependency we can parse", componentText));
            return null;
        }
        ExternalId externalId = externalIdFactory.createMavenExternalId(group, artifact, version);
        return new Dependency(artifact, version, externalId);
    }

    private boolean isLineRelevant(String line) {
        String editableLine = line;

        int indexOfLeftBracket = editableLine.indexOf("[");
        editableLine = editableLine.substring(indexOfLeftBracket + 1);

        int indexOfInfo = editableLine.indexOf("INFO");
        editableLine = editableLine.substring(indexOfInfo + 1);

        int indexOfRightBracket = editableLine.indexOf("]");

        if (indexOfLeftBracket == -1 || indexOfInfo == -1 || indexOfRightBracket == -1) {
            // Does not contain [INFO]
            return false;
        }
        String trimmedLine = editableLine.substring(indexOfRightBracket + 1).trim();
        if (StringUtils.isBlank(trimmedLine) || trimmedLine.contains("Downloaded") || trimmedLine.contains("Downloading")) {
            // Does not have content or this a line about download information
            return false;
        }
        return true;
    }

    private String trimLogLevel(String line) {
        String editableLine = line;

        int indexOfLeftBracket = editableLine.indexOf("[");
        editableLine = editableLine.substring(indexOfLeftBracket + 1);

        int indexOfInfo = editableLine.indexOf("INFO");
        editableLine = editableLine.substring(indexOfInfo + 1);

        int indexOfRightBracket = editableLine.indexOf("]");
        String trimmedLine = editableLine.substring(indexOfRightBracket + 1);

        if (trimmedLine.startsWith(" ")) {
            trimmedLine = trimmedLine.substring(1);
        }
        return trimmedLine;
    }

    private boolean isProjectSection(String line) {
        String editableLine = line;

        int indexOfDashes = editableLine.indexOf("---");
        editableLine = editableLine.substring(indexOfDashes + 1);

        int indexOfDependencyPlugin = editableLine.indexOf("maven-dependency-plugin");
        editableLine = editableLine.substring(indexOfDependencyPlugin + 1);

        int indexOfTreeCommand = editableLine.indexOf(":tree");

        // We only want to parse the dependency:tree output
        if (indexOfDashes == -1 || indexOfDependencyPlugin == -1 || indexOfTreeCommand == -1) {
            // Does not contain --maven-dependency-plugin:tree
            return false;
        }
        return true;
    }

    private boolean isGav(final String componentText) {
        String debugMessage = String.format("%s does not look like a GAV we recognize", componentText);
        String[] gavParts = componentText.split(":");
        if (gavParts.length >= 4) {
            for (String part : gavParts) {
                if (StringUtils.isBlank(part)) {
                    logger.debug(debugMessage);
                    return false;
                }
            }
            return true;
        }
        logger.debug(debugMessage);
        return false;
    }

}
