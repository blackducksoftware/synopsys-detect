package com.synopsys.integration.detect.lifecycle.boot.decision;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.configuration.enumeration.DetectTarget;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.codelocation.BdioCodeLocationCreator;
import com.synopsys.integration.detect.workflow.file.SourceDirectoryDecision;

public class RunDecider {
    private final Logger logger = LoggerFactory.getLogger(BdioCodeLocationCreator.class);

    public RunDecision decide(DetectConfigurationFactory detectConfigurationFactory) {
        DetectToolFilter detectToolFilter = detectConfigurationFactory.createAggregateOptions()
        SourceDirectoryDecision sourceDirectoryDecision = decideSourceDirectory(detectConfigurationFactory.createDetectTarget(), detectConfigurationFactory.createSourceDirectoryOverride());
        BlackduckScanMode blackduckScanMode = detectConfigurationFactory.createScanMode();

        decideTools(blackduckScanMode == BlackduckScanMode.RAPID, detectConfigurationFactory.createAggregateOptions().getDetectToolFilter(), sourceDirectoryDecision.getSourceDirectory().isPresent());

        return new RunDecision();
    }

    public ToolDecision decideTools(boolean isRapid, DetectToolFilter detectToolFilter, SourceDirectoryDecision sourceDirectoryDecision) throws DetectUserFriendlyException {
        ToolDecisionBuilder builder = new ToolDecisionBuilder(); //by default everything is OFF, things must be turned on.

        if (sourceDirectoryDecision.getSourceDirectory().isPresent()) {
            File sourceDirectory = sourceDirectoryDecision.getSourceDirectory().get();
            if (detectToolFilter.shouldInclude(DetectTool.BAZEL)) {
                builder.runBazel(sourceDirectory);
            }

        } else {
        }
        return builder.build();
    }

    public SourceDirectoryDecision decideSourceDirectory(DetectTarget detectTarget, @Nullable Path sourcePathOverride) {
        if (detectTarget == DetectTarget.DIRECTORY) {
            File sourceDirectory = Optional.ofNullable(sourcePathOverride)
                                       .map(Path::toFile)
                                       .orElse(new File(System.getProperty("user.dir")));

            logger.info("Source directory: " + sourceDirectory.getAbsolutePath());
            return SourceDirectoryDecision.withSourceDirectory(sourceDirectory);
        } else {
            return SourceDirectoryDecision.none();
        }

    }

}
