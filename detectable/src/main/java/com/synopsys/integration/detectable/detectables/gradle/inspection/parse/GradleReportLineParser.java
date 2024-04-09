package com.synopsys.integration.detectable.detectables.gradle.inspection.parse;

import java.util.Map;
import java.util.Queue;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectable.util.DetectableStringUtils;
import com.synopsys.integration.detectable.detectables.gradle.inspection.model.GradleTreeNode;

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
    private static final Map<String, String> gradleRichVersions = new HashMap<>();
    private static final Map<String, HashSet<String>> richVersionGroup = new HashMap<>();
    private static final Map<String, String> relationsMap = new HashMap<>();
    public static final String PROJECT_NAME_PREFIX = "projectName:";
    public static final String ROOT_PROJECT_NAME_PREFIX = "rootProjectName:";
    public static final String PROJECT_PARENT_PREFIX = "projectParent:";

    public GradleTreeNode parseLine(String line, Map<String, String> metadata) {
        int level = parseTreeLevel(line);
        if (!line.contains(COMPONENT_PREFIX)) {
            return GradleTreeNode.newUnknown(level);
        } else if (StringUtils.containsAny(line, PROJECT_INDICATORS)) {
            return GradleTreeNode.newProject(level);
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

        String projectName = metadata.getOrDefault(PROJECT_NAME_PREFIX, "orphanProject");
        String rootProjectName = metadata.getOrDefault(ROOT_PROJECT_NAME_PREFIX, "");
        String projectParent = metadata.getOrDefault(PROJECT_PARENT_PREFIX, "null");

        addRelation(projectParent, projectName, rootProjectName);

        if(gavPieces.size() == 3) {
            String dependencyGroupName = gavPieces.get(0) + ":" + gavPieces.get(1);
            if ((cleanedOutput.contains(STRICTLY) || cleanedOutput.contains(REJECT) || cleanedOutput.contains(REQUIRE) || cleanedOutput.contains(PREFER))) {
                gradleRichVersions.putIfAbsent(dependencyGroupName, gavPieces.get(2));
                richVersionGroup.computeIfAbsent(projectName, value -> new HashSet<>()).add(dependencyGroupName);
            }

            if(gradleRichVersions.containsKey(dependencyGroupName)) {
                if(richVersionGroup.containsKey(projectName) && richVersionGroup.get(projectName).contains(dependencyGroupName)) {
                    gavPieces.set(2, gradleRichVersions.get(dependencyGroupName));
                } else if(richVersionGroup.containsKey(rootProjectName) && richVersionGroup.get(rootProjectName).contains(dependencyGroupName)) {
                    gavPieces.set(2, gradleRichVersions.get(dependencyGroupName));
                } else if (checkParentRichVersion(rootProjectName, projectParent, dependencyGroupName)) {
                    gavPieces.set(2, gradleRichVersions.get(dependencyGroupName));
                }
            }
        }

        return gavPieces;
    }

    private void addRelation(String projectParent, String projectName, String rootProjectName) {
        if (!projectParent.equals("null") && !projectParent.contains("root project")) {
            String parentString = projectParent.substring(projectParent.lastIndexOf(":") + 1, projectParent.lastIndexOf("'"));
            relationsMap.putIfAbsent(projectName, parentString);
        } else if (!projectParent.equals("null") && !projectName.equals(rootProjectName)) {
            relationsMap.putIfAbsent(projectName, rootProjectName);
        }
    }

    private boolean checkParentRichVersion(String rootProjectName, String projectParent, String dependencyGroupName) {
       String currentProject = projectParent.substring(projectParent.lastIndexOf(":") + 1, projectParent.lastIndexOf("'"));
       while(!currentProject.equals(rootProjectName)) {
           if(richVersionGroup.containsKey(currentProject) && richVersionGroup.get(currentProject).contains(dependencyGroupName)) {
               return true;
           }
           currentProject = relationsMap.getOrDefault(currentProject, rootProjectName);
       }
       return false;
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
