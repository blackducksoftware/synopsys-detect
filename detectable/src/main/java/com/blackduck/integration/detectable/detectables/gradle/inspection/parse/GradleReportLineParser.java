package com.blackduck.integration.detectable.detectables.gradle.inspection.parse;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackduck.integration.detectable.detectable.util.DetectableStringUtils;
import com.blackduck.integration.detectable.detectables.gradle.inspection.model.GradleTreeNode;

public class GradleReportLineParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String[] TREE_LEVEL_TERMINALS = new String[] { "+---", "\\---" };
    private static final String[] PROJECT_INDICATORS = new String[] { "--- project " };
    private static final String COMPONENT_PREFIX = "--- ";
    private static final String[] REMOVE_SUFFIXES = new String[] { " (*)", " (c)", " (n)" };
    private static final String WINNING_INDICATOR = " -> ";
    private static final String STRICTLY = "strictly";
    private static final String REQUIRE = "require";
    private static final String PREFER = "prefer";
    private static final String REJECT = "reject";

    // This map handles all the dependencies which uses rich version
    // declarations with the respect to the project they are declared in.
    // The nested map has dependencies with their versions and each
    // module will have its own nested map.
    private final Map<String, Map<String, String>> gradleRichVersions = new HashMap<>();

    // This map handles all the transitives whose parent uses rich version
    // declarations with the respect to the project they are declared in.
    // The nested map has dependencies with their versions and each
    // module will have its own nested map.
    private final Map<String, Map<String, String>> transitiveRichVersions = new HashMap<>();

    // This map is handling all the child-parent relationships which are found in the entire project
    // with each child as a key and their parents (if more than one) in a HashSet .
    private final Map<String, Set<String>> relationsMap = new HashMap<>();
    private boolean richVersionUsed = false;
    // tracks the project where rich version was declared
    private String richVersionProject = null;
    // tracks if rich version was declared while parsing the previous line
    private boolean richVersionDeclared = false;
    private String projectName;
    private String rootProjectName;
    private String projectParent;
    private int level;
    private String depthNumber;
    public static final String PROJECT_NAME_PREFIX = "projectName:";
    public static final String ROOT_PROJECT_NAME_PREFIX = "rootProjectName:";
    public static final String PROJECT_PARENT_PREFIX = "projectParent:";
    public static final String FILE_NAME_PREFIX = "fileName:";

    public GradleTreeNode parseLine(String line, Map<String, String> metadata) {
        level = parseTreeLevel(line);
        if (!line.contains(COMPONENT_PREFIX)) {
            return GradleTreeNode.newUnknown(level);
        } else if (StringUtils.containsAny(line, PROJECT_INDICATORS)) {
            String subProjectName = extractSubProjectName(line);
            return GradleTreeNode.newProject(level, subProjectName);
        } else {
            List<String> gav = parseGav(line, metadata);
            if (gav.size() != 3) {
                logger.trace(String.format(
                    "The line can not be reasonably split in to the necessary parts: %s",
                    line
                )); //All project lines: +--- org.springframework.boot:spring-boot-starter-activemq (n)
                return GradleTreeNode.newUnknown(level);
            } else {
                String group = gav.get(0);
                String name = gav.get(1);
                String version = gav.get(2);
                return GradleTreeNode.newGav(level, group, name, version);
            }
        }
    }

    private String extractSubProjectName(String line) {
        // A subProject dependency line looks exactly like: "+--- project :subProjectName" where subProjectName can
        // be a nested subProject (for example  "+--- project :subProjectA:nestedSubProjectB:furtherNestedSubProjectC")
        String[] parts = line.split(PROJECT_INDICATORS[0]);
        if (parts.length == 2) {
            // line looks as expected
            String subprojName = parts[1].trim();
            if (subprojName.startsWith(":")) {
                // Drop the leading ":"
                subprojName = subprojName.substring(1);
                // In a Gradle dependencies tree, dependencies listed previously will have a " (*)" suffix
                subprojName = removeSuffixes(subprojName);
                return subprojName;
            }
        }
        // line didn't look as we expected
        logger.debug("Could not extract subProject name from Gradle dependency tree report for line: " + line);
        return "";
    }

    private String removeSuffixes(String line) {
        for (String suffix : REMOVE_SUFFIXES) {
            if (line.endsWith(suffix)) {
                int lastSeenElsewhereIndex = line.lastIndexOf(suffix);
                line = line.substring(0, lastSeenElsewhereIndex);
            }
        }
        return line;
    }

    private List<String> parseGav(String line, Map<String, String> metadata) {
        String cleanedOutput = StringUtils.trimToEmpty(line);
        cleanedOutput = cleanedOutput.substring(cleanedOutput.indexOf(COMPONENT_PREFIX) + COMPONENT_PREFIX.length());

        cleanedOutput = removeSuffixes(cleanedOutput);

        // we might need to modify the returned list, so it needs to be an actual ArrayList
        List<String> gavPieces = new ArrayList<>(Arrays.asList(cleanedOutput.split(":")));
        if (cleanedOutput.contains(WINNING_INDICATOR)) {
            // WINNING_INDICATOR can point to an entire GAV not just a version
            String winningSection = cleanedOutput.substring(cleanedOutput.indexOf(WINNING_INDICATOR) + WINNING_INDICATOR.length());
            if (winningSection.contains(":")) {
                gavPieces = Arrays.asList(winningSection.split(":"));
            } else {
                // the WINNING_INDICATOR is not always preceded by a : so if isn't, we need to clean up from the original split
                if (gavPieces.get(1).contains(WINNING_INDICATOR)) {
                    String withoutWinningIndicator = gavPieces.get(1).substring(0, gavPieces.get(1).indexOf(WINNING_INDICATOR));
                    gavPieces.set(1, withoutWinningIndicator);
                    // since there was no : we don't have a gav piece for version yet
                    gavPieces.add("");
                }
                gavPieces.set(2, winningSection);
            }
        }

        projectName = metadata.getOrDefault(PROJECT_NAME_PREFIX, "orphanProject"); // get project name from metadata
        rootProjectName = metadata.getOrDefault(ROOT_PROJECT_NAME_PREFIX, "")+"_0"; // get root project name
        projectParent = metadata.getOrDefault(PROJECT_PARENT_PREFIX, "null"); // get project parent name
        String fileName = metadata.getOrDefault(FILE_NAME_PREFIX, "");


        // To avoid a bug caused by an edge case where child and parent modules have the same name causing the loop for checking rich version to stuck
        // in an infinite state, we are going to suffix the name of the project with the depth number
        int s = fileName.lastIndexOf("depth") + 5; // File name is like project__projectname__depth3_dependencyGraph.txt, we extract the number after depth
        int e = fileName.indexOf("_dependencyGraph");
        depthNumber = fileName.substring(s, e);
        projectName = projectName+"_"+depthNumber;

        addRelation();

        // Example of dependency using rich version:
        // --- com.graphql-java:graphql-java:{strictly [21.2, 21.3]; prefer 21.3; reject [20.6, 19.5, 18.2]} -> 21.3 direct depenendency, will be stored in rich versions, richVersionProject value will be current project
        //        +--- com.graphql-java:java-dataloader:3.2.1 transitive needs to be stored
        //          |    \--- org.slf4j:slf4j-api:1.7.30 -> 2.0.4 transitive needs to be stored

        if(gavPieces.size() == 3) {
            String dependencyGroupName = gavPieces.get(0) + ":" + gavPieces.get(1);
            if(level == 0 && checkRichVersionUse(cleanedOutput)) { // we only track rich versions if they are declared in direct dependencies
                storeDirectRichVersion(dependencyGroupName, gavPieces);
            } else {
                storeOrUpdateRichVersion(dependencyGroupName, gavPieces);
            }
        }

        return gavPieces;
    }

    // store the dependency where rich version was declared and update the global tracking values
    private void storeDirectRichVersion(String dependencyGroupName, List<String> gavPieces) {
        gradleRichVersions.computeIfAbsent(projectName, value -> new HashMap<>()).putIfAbsent(dependencyGroupName, gavPieces.get(2));
        richVersionProject = projectName;
        richVersionDeclared = true;
    }

    private void storeOrUpdateRichVersion(String dependencyGroupName, List<String> gavPieces) {
        // this condition is checking for rich version use for current direct dependency in one of the parent submodule of the current module and updates the current version
        checkParentRichVersion(dependencyGroupName, projectName);
        if (richVersionUsed) {
            gavPieces.set(2, gradleRichVersions.get(richVersionProject).get(dependencyGroupName));
            richVersionUsed = false;
        } else if(checkIfTransitiveRichVersion() && transitiveRichVersions.containsKey(richVersionProject) && transitiveRichVersions.get(richVersionProject).containsKey(dependencyGroupName)) {
            // this is checking if we are parsing a transitive dependency and that transitive
            // dependency has already been memoized for the use of rich version
            gavPieces.set(2, transitiveRichVersions.get(richVersionProject).get(dependencyGroupName));
        } else if (checkIfTransitiveRichVersion() && richVersionDeclared) {
            // if while parsing the last direct dependency, we found the use of rich version, we store the version resolved for this transitive dependency
            transitiveRichVersions.computeIfAbsent(richVersionProject, value -> new HashMap<>()).putIfAbsent(dependencyGroupName, gavPieces.get(2));
        } else {
            // no use of rich versions found
            richVersionDeclared = false;
            richVersionProject = null;
        }
    }

    private void addRelation() {
        // add parent-child relationships in the map to keep track of parent and child submodules
        // project parent value will be of two types: one containing "project ':parentName'" or "root project ':rootProjectName'"
        relationsMap.putIfAbsent(projectName, new HashSet<>());
        if (!projectParent.equals("null") && !projectParent.contains("root project")) {
            // this will be the first case where we extract "parentName" from the value
            String parentString = projectParent.substring(projectParent.lastIndexOf(":") + 1, projectParent.lastIndexOf("'"));
            int depth = Integer.parseInt(depthNumber);
            String parentDepth = String.valueOf(depth-1);
            // for submodules who have different parent than root project, we will suffix current depth - 1 to maintain uniformity, so if child is at depth2 than parent would be at depth1
            relationsMap.get(projectName).add(parentString+"_"+parentDepth);
        } else if (!projectParent.equals("null") && !projectName.equals(rootProjectName)) {
            relationsMap.get(projectName).add(rootProjectName);
            // this will be the second case where root project will be the parent
        }
    }

    private void checkParentRichVersion(String dependencyGroupName, String currentProject) {
        // this loop checks all the parent modules for the current submodule upto rootProject for the use of the rich version for the current dependency
        // if the rich version is used return true and update the richVersionProject
        // this loop will stop at the root project and traverse all the parents upto root project, the change was done to support all projects which use a project structure where two child submodules with same name
        if (gradleRichVersions.containsKey(currentProject) && gradleRichVersions.get(currentProject).containsKey(dependencyGroupName)) {
            richVersionProject = currentProject;
            richVersionUsed = true;
        }
        if(!currentProject.equals(rootProjectName)) {
            for (String project : relationsMap.get(currentProject)) {
                checkParentRichVersion(dependencyGroupName, project);
            }
        }
    }

    private boolean checkRichVersionUse(String dependencyLine) {
        return dependencyLine.contains(STRICTLY) || dependencyLine.contains(REJECT) || dependencyLine.contains(REQUIRE) || dependencyLine.contains(PREFER);
    }

    private boolean checkIfTransitiveRichVersion() {
       return richVersionProject != null && level != 0;
    }

    private int parseTreeLevel(String line) {
        if (StringUtils.startsWithAny(line, TREE_LEVEL_TERMINALS)) {
            return 0;
        }

        String modifiedLine = DetectableStringUtils.removeEvery(line, TREE_LEVEL_TERMINALS);

        if (!modifiedLine.startsWith("|") && modifiedLine.startsWith(" ")) {
            modifiedLine = "|" + modifiedLine;
        }
        modifiedLine = modifiedLine.replace("     ", "    |");
        modifiedLine = modifiedLine.replace("||", "|");
        if (modifiedLine.endsWith("|")) {
            modifiedLine = modifiedLine.substring(0, modifiedLine.length() - 5);
        }

        return StringUtils.countMatches(modifiedLine, "|");
    }

}
