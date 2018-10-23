package com.blackducksoftware.integration.hub.detect.workflow.project;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolEvaluation;
import com.synopsys.integration.util.NameVersion;

public class ProjectNameVersionManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProjectNameVersionOptions projectVersionOptions;
    private final BomToolNameVersionDecider bomToolNameVersionDecider;

    public ProjectNameVersionManager(final ProjectNameVersionOptions projectVersionOptions, final BomToolNameVersionDecider bomToolNameVersionDecider) {
        this.projectVersionOptions = projectVersionOptions;
        this.bomToolNameVersionDecider = bomToolNameVersionDecider;
    }

    public NameVersion calculateDefaultProjectNameVersion() {
        return evaluateProjectNameVersion(new ArrayList<>());
    }

    public NameVersion evaluateProjectNameVersion(final List<BomToolEvaluation> bomToolEvaluations) {
        Optional<NameVersion> bomToolSuggestedNameVersion = Optional.empty();
        if (bomToolEvaluations.size() > 0) {
            bomToolSuggestedNameVersion = findBomToolProjectNameAndVersion(bomToolEvaluations);
        }

        String projectName = projectVersionOptions.overrideProjectName;
        if (StringUtils.isBlank(projectName) && bomToolSuggestedNameVersion.isPresent()) {
            projectName = bomToolSuggestedNameVersion.get().getName();
        }

        if (StringUtils.isBlank(projectName)) {
            logger.info("A project name could not be decided. Using the name of the source path.");
            projectName = projectVersionOptions.sourcePathName;
        }

        String projectVersionName = projectVersionOptions.overrideProjectVersionName;
        if (StringUtils.isBlank(projectVersionName) && bomToolSuggestedNameVersion.isPresent()) {
            projectVersionName = bomToolSuggestedNameVersion.get().getVersion();
        }

        if (StringUtils.isBlank(projectVersionName)) {
            if ("timestamp".equals(projectVersionOptions.defaultProjectVersionScheme)) {
                logger.info("A project version name could not be decided. Using the current timestamp.");
                final String timeformat = projectVersionOptions.defaultProjectVersionFormat;
                final String timeString = DateTimeFormatter.ofPattern(timeformat).withZone(ZoneOffset.UTC).format(Instant.now().atZone(ZoneOffset.UTC));
                projectVersionName = timeString;
            } else {
                logger.info("A project version name could not be decided. Using the default version text.");
                projectVersionName = projectVersionOptions.defaultProjectVersionText;
            }
        }

        return new NameVersion(projectName, projectVersionName);
    }

    private Optional<NameVersion> findBomToolProjectNameAndVersion(final List<BomToolEvaluation> bomToolEvaluations) {
        final String projectBomTool = projectVersionOptions.projectBomTool;
        BomToolGroupType preferredBomToolType = null;
        if (StringUtils.isNotBlank(projectBomTool)) {
            final String projectBomToolFixed = projectBomTool.toUpperCase();
            if (!BomToolGroupType.POSSIBLE_NAMES.contains(projectBomToolFixed)) {
                logger.info("A valid preferred bom tool type was not provided, deciding project name automatically.");
            } else {
                preferredBomToolType = BomToolGroupType.valueOf(projectBomToolFixed);
            }
        }

        final List<BomToolProjectInfo> allBomToolProjectInfo = createBomToolProjectInfo(bomToolEvaluations);
        return bomToolNameVersionDecider.decideProjectNameVersion(allBomToolProjectInfo, preferredBomToolType);
    }

    private List<BomToolProjectInfo> createBomToolProjectInfo(final List<BomToolEvaluation> bomToolEvaluations) {
        return bomToolEvaluations.stream()
                   .filter(it -> it.wasExtractionSuccessful())
                   .filter(it -> it.getExtraction().projectName != null)
                   .map(it -> {
                       final NameVersion nameVersion = new NameVersion(it.getExtraction().projectName, it.getExtraction().projectVersion);
                       final BomToolProjectInfo possibility = new BomToolProjectInfo(it.getBomTool().getBomToolGroupType(), it.getEnvironment().getDepth(), nameVersion);
                       return possibility;
                   })
                   .collect(Collectors.toList());
    }
}
