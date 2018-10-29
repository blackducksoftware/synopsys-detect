package com.blackducksoftware.integration.hub.detect.workflow.project;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.DetectTool;
import com.synopsys.integration.util.NameVersion;

public class ProjectNameVersionDecider {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProjectNameVersionOptions projectVersionOptions;

    public ProjectNameVersionDecider(final ProjectNameVersionOptions projectVersionOptions) {
        this.projectVersionOptions = projectVersionOptions;
    }

    public NameVersion decideProjectNameVersion(String preferredDetectTools, final List<DetectToolProjectInfo> detectToolProjectInfo) {

        Optional<String> decidedProjectName = Optional.empty();
        Optional<String> decidedProjectVersion = Optional.empty();

        if (StringUtils.isNotBlank(projectVersionOptions.overrideProjectName)) {
            decidedProjectName = Optional.of(projectVersionOptions.overrideProjectName);
        }

        if (StringUtils.isNotBlank(projectVersionOptions.overrideProjectVersionName)) {
            decidedProjectVersion = Optional.of(projectVersionOptions.overrideProjectVersionName);
        }

        Optional<DetectToolProjectInfo> chosenTool = decideToolProjectInfo(preferredDetectTools, detectToolProjectInfo);
        if (chosenTool.isPresent()) {
            if (!decidedProjectName.isPresent()) {
                decidedProjectName = Optional.ofNullable(chosenTool.get().getSuggestedNameVersion().getName());
            }
            if (!decidedProjectVersion.isPresent()) {
                decidedProjectVersion = Optional.ofNullable(chosenTool.get().getSuggestedNameVersion().getVersion());
            }
        }

        if (!decidedProjectName.isPresent()) {
            logger.info("A project name could not be decided. Using the name of the source path.");
            decidedProjectName = Optional.of(projectVersionOptions.sourcePathName);
        }

        if (!decidedProjectVersion.isPresent()) {
            if ("timestamp".equals(projectVersionOptions.defaultProjectVersionScheme)) {
                logger.info("A project version name could not be decided. Using the current timestamp.");
                final String timeformat = projectVersionOptions.defaultProjectVersionFormat;
                final String timeString = DateTimeFormatter.ofPattern(timeformat).withZone(ZoneOffset.UTC).format(Instant.now().atZone(ZoneOffset.UTC));
                decidedProjectVersion = Optional.of(timeString);
            } else {
                logger.info("A project version name could not be decided. Using the default version text.");
                decidedProjectVersion = Optional.of(projectVersionOptions.defaultProjectVersionText);
            }
        }

        return new NameVersion(decidedProjectName.get(), decidedProjectVersion.get());
    }

    private Optional<DetectToolProjectInfo> findProjectInfoForTool(DetectTool tool, List<DetectToolProjectInfo> detectToolProjectInfo) {
        return detectToolProjectInfo.stream()
                   .filter(it -> it.getDetectTool().equals(tool))
                   .findFirst();
    }

    private Optional<DetectToolProjectInfo> decideToolProjectInfo(String preferredDetectTools, List<DetectToolProjectInfo> detectToolProjectInfo) {
        Optional<DetectToolProjectInfo> chosenTool = Optional.empty();

        List<DetectTool> toolOrder = DetectTool.DEFAULT_PROJECT_ORDER;
        if (StringUtils.isNotBlank(preferredDetectTools)) {
            String[] tools = preferredDetectTools.split(",");
            toolOrder = Arrays.asList(tools).stream().map(it -> DetectTool.valueOf(it)).collect(Collectors.toList());
        }

        for (DetectTool tool : toolOrder) {
            chosenTool = findProjectInfoForTool(tool, detectToolProjectInfo);

            if (chosenTool.isPresent()) {
                logger.info("Using the first ordered tool with project info: " + tool.toString());
                break;
            }
        }

        return chosenTool;
    }
}
