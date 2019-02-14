/**
 * detect-application
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.synopsys.integration.detect.detector.maven;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocationType;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.util.ExcludedIncludedFilter;

public class MavenCodeLocationPackager {
    public static final List<String> indentationStrings = Arrays.asList("+- ", "|  ", "\\- ", "   ");
    public static final List<String> KNOWN_SCOPES = Arrays.asList("compile", "provided", "runtime", "test", "system", "import");

    private static final Logger logger = LoggerFactory.getLogger(MavenCodeLocationPackager.class);
    public static final String ORPHAN_LIST_PARENT_NODE_NAME = "Additional_Components";
    public static final String ORPHAN_LIST_PARENT_NODE_GROUP = "none";
    public static final String ORPHAN_LIST_PARENT_NODE_VERSION = "none";

    private final ExternalIdFactory externalIdFactory;
    private List<MavenParseResult> codeLocations = new ArrayList<>();
    private MavenParseResult currentMavenProject = null;
    private Stack<Dependency> dependencyParentStack = new Stack<>();
    // in-scope components found in an out-of-scope tree go in the orphans list
    private final List<Dependency> orphans = new ArrayList<>();
    private boolean parsingProjectSection;
    private int level;
    private boolean inOutOfScopeTree=false;
    private MutableDependencyGraph currentGraph = null;

    public MavenCodeLocationPackager(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    // mavenTextOutput should be the full output of mvn dependency:tree (no scope applied); scope filtering is now done by this method
    public List<MavenParseResult> extractCodeLocations(final String sourcePath, final String mavenOutputText, final String targetScope, final String excludedModules, final String includedModules) {
        final ExcludedIncludedFilter filter = new ExcludedIncludedFilter(excludedModules, includedModules);
        codeLocations = new ArrayList<>();
        currentMavenProject = null;
        dependencyParentStack = new Stack<>();
        parsingProjectSection = false;
        currentGraph = new MutableMapDependencyGraph();

        level = 0;
        for (final String currentLine : mavenOutputText.split(System.lineSeparator())) {
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
            if (isDependencyTreeUpdates(line)) {
                continue;
            }

            if (parsingProjectSection && currentMavenProject == null) {
                // this is the first line of a new code location, the following lines will be the tree of dependencies for this code location
                currentGraph = new MutableMapDependencyGraph();
                final MavenParseResult mavenProject = createMavenParseResult(sourcePath, line, currentGraph);
                if (null != mavenProject && filter.shouldInclude(mavenProject.projectName)) {
                    logger.trace(String.format("Project: %s", mavenProject.projectName));
                    this.currentMavenProject = mavenProject;
                    codeLocations.add(mavenProject);
                } else {
                    logger.trace("Project: unknown");
                    currentMavenProject = null;
                    dependencyParentStack.clear();
                    parsingProjectSection = false;
                    level = 0;
                }
                continue;
            }

            final boolean finished = line.contains("--------");
            if (finished) {
                currentMavenProject = null;
                dependencyParentStack.clear();
                parsingProjectSection = false;
                level = 0;
                continue;
            }

            final int previousLevel = level;
            final String cleanedLine = calculateCurrentLevelAndCleanLine(line);
            final ScopedDependency dependency = textToDependency(cleanedLine);
            if (null == dependency) {
                continue;
            }
            if (currentMavenProject != null) {
                if (level == 1) {
                    // a direct dependency, clear the stack and add this as a potential parent for the next line
                    if (dependency.isInScope(targetScope)) {
                        logger.trace(String.format("Level 1 component %s:%s:%s:%s is in scope; adding it to hierarchy root", dependency.externalId.group, dependency.externalId.name, dependency.externalId.version, dependency.scope));
                        currentGraph.addChildToRoot(dependency);
                        inOutOfScopeTree = false;
                    } else {
                        logger.trace(String.format("Level 1 component %s:%s:%s:%s is a top-level out-of-scope component; entering non-scoped tree", dependency.externalId.group, dependency.externalId.name, dependency.externalId.version, dependency.scope));
                        inOutOfScopeTree = true;
                    }
                    dependencyParentStack.clear();
                    dependencyParentStack.push(dependency);
                } else {
                    // level should be greater than 1
                    if (level == previousLevel) {
                        // a sibling of the previous dependency
                        dependencyParentStack.pop();
                        addDependencyIfInScope(currentGraph, orphans, targetScope, inOutOfScopeTree, dependencyParentStack.peek(), dependency);
                        dependencyParentStack.push(dependency);
                    } else if (level > previousLevel) {
                        // a child of the previous dependency
                        addDependencyIfInScope(currentGraph, orphans, targetScope, inOutOfScopeTree, dependencyParentStack.peek(), dependency);
                        dependencyParentStack.push(dependency);
                    } else {
                        // a child of a dependency further back than 1 line
                        for (int i = previousLevel; i >= level; i--) {
                            dependencyParentStack.pop();
                        }
                        addDependencyIfInScope(currentGraph, orphans, targetScope, inOutOfScopeTree, dependencyParentStack.peek(), dependency);
                        dependencyParentStack.push(dependency);
                    }
                }
            }
        }
        addOrphansToGraph(currentGraph, orphans);

        return codeLocations;
    }

    private void addOrphansToGraph(final MutableDependencyGraph graph, final List<Dependency> orphans) {
        logger.trace(String.format("# orphans: %d", orphans.size()));
        if (orphans.size() > 0) {
            final Dependency orphanListParent = createOrphanListParentDependency();
            logger.trace(String.format("adding orphan list parent dependency: %s", orphanListParent.externalId.toString()));
            graph.addChildToRoot(orphanListParent);
            for (Dependency dependency : orphans) {
                logger.trace(String.format("adding orphan: %s", dependency.externalId.toString()));
                graph.addParentWithChild(orphanListParent, dependency);
            }
        }
    }

    private void addDependencyIfInScope(final MutableDependencyGraph currentGraph, final List<Dependency> orphans, final String targetScope, final boolean inOutOfScopeTree, final Dependency parent, final ScopedDependency dependency) {
        if (dependency.isInScope(targetScope)) {
            if (inOutOfScopeTree) {
                logger.trace(
                    String.format("component %s:%s:%s:%s is in scope but in a nonScope tree; adding it to orphans", dependency.externalId.group, dependency.externalId.name, dependency.externalId.version, dependency.scope));
                orphans.add(dependency);
            } else {
                logger.trace(String.format("component %s:%s:%s:%s is in scope and in an in-scope tree; adding it to hierarchy", dependency.externalId.group, dependency.externalId.name, dependency.externalId.version, dependency.scope));
                currentGraph.addParentWithChild(parent, dependency);
            }
        }
    }

    private MavenParseResult createMavenParseResult(final String sourcePath, final String line, final DependencyGraph graph) {
        final Dependency dependency = textToProject(line);
        if (null != dependency) {
            String codeLocationSourcePath = sourcePath;
            if (!sourcePath.endsWith(dependency.name)) {
                codeLocationSourcePath += "/" + dependency.name;
            }
            final DetectCodeLocation codeLocation = new DetectCodeLocation.Builder(DetectCodeLocationType.MAVEN, codeLocationSourcePath, dependency.externalId, graph).build();
            return new MavenParseResult(dependency.name, dependency.version, codeLocation);
        }
        return null;
    }

    String calculateCurrentLevelAndCleanLine(final String line) {
        level = 0;
        String cleanedLine = line;
        for (final String pattern : indentationStrings) {
            while (cleanedLine.contains(pattern)) {
                level++;
                cleanedLine = cleanedLine.replaceFirst(Pattern.quote(pattern), "");
            }
        }

        return cleanedLine;
    }

    private Dependency createOrphanListParentDependency() {
        final ExternalId externalId = externalIdFactory.createMavenExternalId(ORPHAN_LIST_PARENT_NODE_GROUP, ORPHAN_LIST_PARENT_NODE_NAME, ORPHAN_LIST_PARENT_NODE_VERSION);
        return new Dependency(ORPHAN_LIST_PARENT_NODE_NAME, ORPHAN_LIST_PARENT_NODE_VERSION, externalId);
    }

    ScopedDependency textToDependency(final String componentText) {
        if (!isGav(componentText)) {
            return null;
        }
        final String[] gavParts = componentText.split(":");
        final String group = gavParts[0];
        final String artifact = gavParts[1];

        final String scope = gavParts[gavParts.length - 1];
        final boolean recognizedScope = KNOWN_SCOPES.stream().anyMatch(knownScope -> scope.startsWith(knownScope));

        if (!recognizedScope) {
            logger.warn("This line can not be parsed correctly due to an unknown dependency format - it is unlikely a match will be found for this dependency: " + componentText);
        }
        final String version = gavParts[gavParts.length - 2];
        final ExternalId externalId = externalIdFactory.createMavenExternalId(group, artifact, version);
        return new ScopedDependency(artifact, version, externalId, scope);
    }

    Dependency textToProject(final String componentText) {
        if (!isGav(componentText)) {
            return null;
        }
        final String[] gavParts = componentText.split(":");
        final String group = gavParts[0];
        final String artifact = gavParts[1];
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
        final ExternalId externalId = externalIdFactory.createMavenExternalId(group, artifact, version);
        return new Dependency(artifact, version, externalId);
    }

    boolean isLineRelevant(final String line) {
        final String editableLine = line;
        if (!doesLineContainSegmentsInOrder(line, "[", "INFO", "]")) {
            // Does not contain [INFO]
            return false;
        }
        final int index = indexOfEndOfSegments(line, "[", "INFO", "]");
        final String trimmedLine = editableLine.substring(index);

        if (StringUtils.isBlank(trimmedLine) || trimmedLine.contains("Downloaded") || trimmedLine.contains("Downloading")) {
            // Does not have content or this a line about download information
            return false;
        }
        return true;
    }

    String trimLogLevel(final String line) {
        final String editableLine = line;

        final int index = indexOfEndOfSegments(line, "[", "INFO", "]");
        String trimmedLine = editableLine.substring(index);

        if (trimmedLine.startsWith(" ")) {
            trimmedLine = trimmedLine.substring(1);
        }
        return trimmedLine;
    }

    boolean isProjectSection(final String line) {
        // We only want to parse the dependency:tree output
        return doesLineContainSegmentsInOrder(line, "---", "dependency", ":", "tree");
    }

    boolean isDependencyTreeUpdates(final String line) {
        if (line.contains("checking for updates")) {
            return true;
        } else {
            return false;
        }
    }

    boolean isGav(final String componentText) {
        final String debugMessage = String.format("%s does not look like a GAV we recognize", componentText);
        final String[] gavParts = componentText.split(":");
        if (gavParts.length >= 4) {
            for (final String part : gavParts) {
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

    boolean doesLineContainSegmentsInOrder(final String line, final String... segments) {
        Boolean lineContainsSegments = true;

        final int index = indexOfEndOfSegments(line, segments);
        if (index == -1) {
            lineContainsSegments = false;
        }

        return lineContainsSegments;
    }

    int indexOfEndOfSegments(final String line, final String... segments) {
        int endOfSegments = -1;
        if (segments.length > 0) {
            endOfSegments = 0;
        }

        String editableLine = line;
        for (final String segment : segments) {
            final int index = editableLine.indexOf(segment);
            // If the string does not contain the segment indexOf returns -1
            if (index == -1) {
                endOfSegments = -1;
                break;
            }
            // Add the index to the total to keep track of the index in the original String
            endOfSegments += (index + segment.length());

            // cut the string off right after the segment we just found so we are only looking at the remainder of the line for the next segment
            editableLine = editableLine.substring(index + segment.length());
        }
        return endOfSegments;
    }

}
