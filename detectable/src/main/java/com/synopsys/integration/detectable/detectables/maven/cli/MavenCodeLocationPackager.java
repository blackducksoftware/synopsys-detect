package com.synopsys.integration.detectable.detectables.maven.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.util.ExcludedIncludedWildcardFilter;

// TODO: Re-write. Some fields could be local variables. Includes many code smells. A component none:Additional_Components:none appears in the graph.
public class MavenCodeLocationPackager {
    private static final List<String> indentationStrings = Arrays.asList("+- ", "|  ", "\\- ", "   ");
    private static final List<String> KNOWN_SCOPES = Arrays.asList("compile", "provided", "runtime", "test", "system", "import");

    private static final Logger logger = LoggerFactory.getLogger(MavenCodeLocationPackager.class);
    public static final String ORPHAN_LIST_PARENT_NODE_NAME = "Additional_Components";
    public static final String ORPHAN_LIST_PARENT_NODE_GROUP = "none";
    public static final String ORPHAN_LIST_PARENT_NODE_VERSION = "none";

    private static final String END_OF_TREE_PATTERN_STRING = "^-*< .* >-*$";
    private final Pattern endOfTreePattern = Pattern.compile(END_OF_TREE_PATTERN_STRING);
    private final ExternalIdFactory externalIdFactory;
    private List<MavenParseResult> codeLocations = new ArrayList<>();
    private MavenParseResult currentMavenProject = null;
    private Stack<Dependency> dependencyParentStack = new Stack<>();
    // in-scope components found in an out-of-scope tree go in the orphans list
    private final List<Dependency> orphans = new ArrayList<>();
    private boolean parsingProjectSection;
    private int level;
    private boolean inOutOfScopeTree = false;
    private DependencyGraph currentGraph = null;

    public MavenCodeLocationPackager(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    // mavenOutput should be the full output of mvn dependency:tree (no scope applied); scope filtering is now done by this method
    public List<MavenParseResult> extractCodeLocations(
        String sourcePath,
        List<String> mavenOutput,
        List<String> excludedScopes,
        List<String> includedScopes,
        List<String> excludedModules,
        List<String> includedModules
    ) {
        ExcludedIncludedWildcardFilter modulesFilter = ExcludedIncludedWildcardFilter.fromCollections(excludedModules, includedModules);
        ExcludedIncludedWildcardFilter scopeFilter = ExcludedIncludedWildcardFilter.fromCollections(excludedScopes, includedScopes);
        codeLocations = new ArrayList<>();
        currentMavenProject = null;
        dependencyParentStack = new Stack<>();
        parsingProjectSection = false;
        currentGraph = new BasicDependencyGraph();

        level = 0;
        for (String currentLine : mavenOutput) {
            String line = currentLine.trim();

            if (shouldSkipLine(line)) {
                continue;
            }

            line = trimLogLevel(line);

            if (parsingProjectSection && currentMavenProject == null) {
                initializeCurrentMavenProject(modulesFilter, sourcePath, line);
                continue;
            }

            boolean finished = line.contains("--------") || endOfTreePattern.matcher(line).matches();
            if (finished) {
                currentMavenProject = null;
                dependencyParentStack.clear();
                parsingProjectSection = false;
                level = 0;
                continue;
            }

            int previousLevel = level;
            String cleanedLine = calculateCurrentLevelAndCleanLine(line);
            String cleanedLineWithoutParentheses = removeParenthesesForTransitiveDependencies(cleanedLine);
            ScopedDependency dependency = textToDependency(cleanedLineWithoutParentheses);

            if (null == dependency) {
                continue;
            }
            if (currentMavenProject != null) {
                populateGraphDependencies(scopeFilter, dependency, previousLevel);
            }
        }
        addOrphansToGraph(currentGraph, orphans);

        return codeLocations;
    }

    private boolean shouldSkipLine(String line) {
        if (!isLineRelevant(line)) {
            return true;
        }
        line = trimLogLevel(line);
        if (StringUtils.isBlank(line)) {
            return true;
        }
        if (isProjectSection(line)) {
            parsingProjectSection = true;
            return true;
        }
        if (!parsingProjectSection) {
            return true;
        }
        if (isDependencyTreeUpdates(line)) {
            return true;
        }
        return false;
    }

    private void initializeCurrentMavenProject(ExcludedIncludedWildcardFilter modulesFilter, String sourcePath, String line) {
        // this is the first line of a new code location, the following lines will be the tree of dependencies for this code location
        currentGraph = new BasicDependencyGraph();
        MavenParseResult mavenProject = createMavenParseResult(sourcePath, line, currentGraph);
        if (null != mavenProject && modulesFilter.shouldInclude(mavenProject.getProjectName())) {
            logger.trace(String.format("Project: %s", mavenProject.getProjectName()));
            this.currentMavenProject = mavenProject;
            codeLocations.add(mavenProject);
        } else {
            logger.trace("Project: unknown");
            currentMavenProject = null;
            dependencyParentStack.clear();
            parsingProjectSection = false;
            level = 0;
        }
    }

    private void populateGraphDependencies(ExcludedIncludedWildcardFilter scopeFilter, ScopedDependency dependency, int previousLevel) {
        if (level == 1) {
            // a direct dependency, clear the stack and add this as a potential parent for the next line
            if (scopeFilter.shouldInclude(dependency.scope)) {
                logger.trace(String.format(
                    "Level 1 component %s:%s:%s:%s is in scope; adding it to hierarchy root",
                    dependency.getExternalId().getGroup(),
                    dependency.getExternalId().getName(),
                    dependency.getExternalId().getVersion(),
                    dependency.scope
                ));
                currentGraph.addChildToRoot(dependency);
                inOutOfScopeTree = false;
            } else {
                logger.trace(String.format(
                    "Level 1 component %s:%s:%s:%s is a top-level out-of-scope component; entering non-scoped tree",
                    dependency.getExternalId().getGroup(),
                    dependency.getExternalId().getName(),
                    dependency.getExternalId().getVersion(),
                    dependency.scope
                ));
                inOutOfScopeTree = true;
            }
            dependencyParentStack.clear();
            dependencyParentStack.push(dependency);
        } else if (level != 0) {
            // level should be greater than 1
            if (level == previousLevel) {
                // a sibling of the previous dependency
                dependencyParentStack.pop();
                addDependencyIfInScope(currentGraph, orphans, scopeFilter, inOutOfScopeTree, dependencyParentStack.peek(), dependency);
                dependencyParentStack.push(dependency);
            } else if (level > previousLevel) {
                // a child of the previous dependency
                addDependencyIfInScope(currentGraph, orphans, scopeFilter, inOutOfScopeTree, dependencyParentStack.peek(), dependency);
                dependencyParentStack.push(dependency);
            } else {
                // a child of a dependency further back than 1 line
                for (int i = previousLevel; i >= level; i--) {
                    dependencyParentStack.pop();
                }
                addDependencyIfInScope(currentGraph, orphans, scopeFilter, inOutOfScopeTree, dependencyParentStack.peek(), dependency);
                dependencyParentStack.push(dependency);
            }
        }
    }

    private void addOrphansToGraph(DependencyGraph graph, List<Dependency> orphans) {
        logger.trace(String.format("# orphans: %d", orphans.size()));
        if (orphans.size() > 0) {
            Dependency orphanListParent = createOrphanListParentDependency();
            logger.trace(String.format("adding orphan list parent dependency: %s", orphanListParent.getExternalId().toString()));
            graph.addChildToRoot(orphanListParent);
            for (Dependency dependency : orphans) {
                logger.trace(String.format("adding orphan: %s", dependency.getExternalId().toString()));
                graph.addParentWithChild(orphanListParent, dependency);
            }
        }
    }

    private void addDependencyIfInScope(
        DependencyGraph currentGraph,
        List<Dependency> orphans,
        ExcludedIncludedWildcardFilter scopeFilter,
        boolean inOutOfScopeTree,
        Dependency parent,
        ScopedDependency dependency
    ) {
        if (scopeFilter.shouldInclude(dependency.scope)) {
            if (inOutOfScopeTree) {
                logger.trace(
                    String.format(
                        "component %s:%s:%s:%s is in scope but in a nonScope tree; adding it to orphans",
                        dependency.getExternalId().getGroup(),
                        dependency.getExternalId().getName(),
                        dependency.getExternalId().getVersion(),
                        dependency.scope
                    ));
                orphans.add(dependency);
            } else {
                logger.trace(String.format(
                    "component %s:%s:%s:%s is in scope and in an in-scope tree; adding it to hierarchy",
                    dependency.getExternalId().getGroup(),
                    dependency.getExternalId().getName(),
                    dependency.getExternalId().getVersion(),
                    dependency.scope
                ));
                currentGraph.addParentWithChild(parent, dependency);
            }
        }
    }

    private MavenParseResult createMavenParseResult(String sourcePath, String line, DependencyGraph graph) {
        Dependency dependency = textToProject(line);
        if (null != dependency) {
            String codeLocationSourcePath = sourcePath;
            if (!sourcePath.endsWith(dependency.getName())) {
                codeLocationSourcePath += "/" + dependency.getName();
            }
            CodeLocation codeLocation = new CodeLocation(graph, dependency.getExternalId(), new File(codeLocationSourcePath));
            return new MavenParseResult(dependency.getName(), dependency.getVersion(), codeLocation);
        }
        return null;
    }

    public String calculateCurrentLevelAndCleanLine(String line) {
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

    private Dependency createOrphanListParentDependency() {
        ExternalId externalId = externalIdFactory.createMavenExternalId(ORPHAN_LIST_PARENT_NODE_GROUP, ORPHAN_LIST_PARENT_NODE_NAME, ORPHAN_LIST_PARENT_NODE_VERSION);
        return new Dependency(ORPHAN_LIST_PARENT_NODE_NAME, ORPHAN_LIST_PARENT_NODE_VERSION, externalId);
    }

    public ScopedDependency textToDependency(String componentText) {
        if (!isGav(componentText)) {
            return null;
        }
        
        // This is a GAV line.
        componentText = removeGroupArtifactPipedSuffixIfExists(componentText);
        
        String[] gavParts = componentText.split(":");
        String group = gavParts[0];
        String artifact = gavParts[1];
        String scope = null;
        String version;
        if (gavParts.length > 4) {
            scope = gavParts[gavParts.length - 1];
            boolean recognizedScope = KNOWN_SCOPES.stream().anyMatch(scope::startsWith);
            if (!recognizedScope) {
                logger.warn("This line can not be parsed correctly due to an unknown dependency format - it is unlikely a match will be found for this dependency: " + componentText);
            }
            version = gavParts[gavParts.length - 2];
        } else {
            logger.warn("This line does not specify a scope - it is possible that a match is not found for this dependency: " + componentText);
            version = gavParts[gavParts.length - 1];
        }
        ExternalId externalId = externalIdFactory.createMavenExternalId(group, artifact, version);
        return new ScopedDependency(artifact, version, externalId, scope);
    }
    
    private String removeGroupArtifactPipedSuffixIfExists(String componentText) {
        int pipeFirstPosition = -1;
        if ((pipeFirstPosition = componentText.indexOf("|")) > -1) {
            // There should be a closing |
            int pipeLastPosition = -1;
            if ((pipeLastPosition = componentText.lastIndexOf("|")) > -1) {
                // Check if the same | was found twice.
                if (pipeFirstPosition != pipeLastPosition) {
                    // This line has a suffix of the format |group:artifact| for each line. Deal with it.
                    componentText = componentText.substring(0, pipeFirstPosition).trim();
                }
            }
        }
        return componentText;
    }

    public Dependency textToProject(String componentText) {
        if (!isGav(componentText)) {
            return null;
        }
        
        // This is a GAV line.
        componentText = removeGroupArtifactPipedSuffixIfExists(componentText);
        
        String[] gavParts = componentText.split(":");
        String group = gavParts[0];
        String artifact = gavParts[1];        
        String version;
        if (gavParts.length == 4) {
            // Dependency does not include the classifier
            version = gavParts[gavParts.length - 1];
        } else if (gavParts.length == 5) {
            // Dependency does include the classifier
            version = gavParts[gavParts.length - 1]; //Should be 2. Possible sleeper.
        } else {
            logger.debug(String.format("%s does not look like a dependency we can parse", componentText));
            return null;
        }
        ExternalId externalId = externalIdFactory.createMavenExternalId(group, artifact, version);
        return new Dependency(artifact, version, externalId);
    }

    public boolean isLineRelevant(String line) {
        String editableLine = line;
        if (!doesLineContainSegmentsInOrder(line, "[", "INFO", "]")) {
            // Does not contain [INFO]
            return false;
        }
        int index = indexOfEndOfSegments(line, "[", "INFO", "]");
        String trimmedLine = editableLine.substring(index);

        // Does not have content or this a line about download information
        return !StringUtils.isBlank(trimmedLine) && !trimmedLine.contains("Downloaded") && !trimmedLine.contains(
            "Downloading");
    }

    public String trimLogLevel(String line) {
        String editableLine = line;

        int index = indexOfEndOfSegments(line, "[", "INFO", "]");
        String trimmedLine = editableLine.substring(index);

        if (trimmedLine.startsWith(" ")) {
            trimmedLine = trimmedLine.substring(1);
        }
        return trimmedLine;
    }

    public boolean isProjectSection(String line) {
        // We only want to parse the dependency:tree output
        return doesLineContainSegmentsInOrder(line, "---", "dependency", ":", "tree");
    }

    public boolean isDependencyTreeUpdates(String line) {
        return line.contains("checking for updates");
    }

    public boolean isGav(String componentText) {
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

    public boolean doesLineContainSegmentsInOrder(String line, String... segments) {
        boolean lineContainsSegments = true;

        int index = indexOfEndOfSegments(line, segments);
        if (index == -1) {
            lineContainsSegments = false;
        }

        return lineContainsSegments;
    }

    public int indexOfEndOfSegments(String line, String... segments) {
        int endOfSegments = -1;
        if (segments.length > 0) {
            endOfSegments = 0;
        }

        String editableLine = line;
        for (String segment : segments) {
            int index = editableLine.indexOf(segment);
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

    public String removeParenthesesForTransitiveDependencies(String cleanedLineWithParentheses) {
        //command 'mvnw -Dverbose dependency:tree' returns  'Maven coordinates' that starts and ends with parentheses.
        //In the BDIO file, external ID has "(" character as prefix for transitive dependencies. Therefore, "(" is causing matching issues in KB.
        //This method checks for this pattern and removes the beginning and ending parentheses.

        Pattern lineThatStartsAndEndsWithParentheses = Pattern.compile("^\\(.*\\)$");

        return lineThatStartsAndEndsWithParentheses.matcher(cleanedLineWithParentheses).matches()
                ? cleanedLineWithParentheses.substring(1, cleanedLineWithParentheses.length() - 1) : cleanedLineWithParentheses;
    }
}
