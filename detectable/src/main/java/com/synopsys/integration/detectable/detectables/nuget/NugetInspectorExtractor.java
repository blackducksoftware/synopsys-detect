/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.nuget;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraphCombiner;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspector;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorOptions;
import com.synopsys.integration.detectable.detectables.nuget.parse.NugetInspectorParser;
import com.synopsys.integration.detectable.detectables.nuget.parse.NugetParseResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;
import com.synopsys.integration.util.NameVersion;

public class NugetInspectorExtractor {
    public static final String INSPECTOR_OUTPUT_PATTERN = "*_inspection.json";

    private final Logger logger = LoggerFactory.getLogger(NugetInspectorExtractor.class);

    private final NugetInspectorParser nugetInspectorParser;
    private final FileFinder fileFinder;

    public NugetInspectorExtractor(final NugetInspectorParser nugetInspectorParser, final FileFinder fileFinder) {
        this.nugetInspectorParser = nugetInspectorParser;
        this.fileFinder = fileFinder;
    }

    public Extraction extract(final List<File> targets, final File outputDirectory, final NugetInspector inspector, final NugetInspectorOptions nugetInspectorOptions) {
        try {
            final List<NugetTargetResult> results = new ArrayList<>();

            for (int i = 0; i < targets.size(); i++) {
                final File targetDirectory = new File(outputDirectory, "inspection-" + i);
                results.add(executeTarget(inspector, targets.get(i), targetDirectory, nugetInspectorOptions));
            }

            final List<CodeLocation> codeLocations = results.stream()
                                                         .flatMap(it -> it.codeLocations.stream())
                                                         .collect(Collectors.toList());

            final Map<File, CodeLocation> codeLocationsBySource = new HashMap<>();
            final DependencyGraphCombiner combiner = new DependencyGraphCombiner();

            codeLocations.forEach(codeLocation -> {
                final File sourcePathFile = codeLocation.getSourcePath().orElse(null);
                if (codeLocationsBySource.containsKey(sourcePathFile)) {
                    logger.debug("Combined code location for: " + sourcePathFile);
                    final CodeLocation destination = codeLocationsBySource.get(sourcePathFile);
                    combiner.addGraphAsChildrenToRoot((MutableDependencyGraph) destination.getDependencyGraph(), codeLocation.getDependencyGraph());
                } else {
                    codeLocationsBySource.put(sourcePathFile, codeLocation);
                }
            });

            final Optional<NameVersion> nameVersion = results.stream()
                                                          .filter(it -> it.nameVersion != null)
                                                          .map(it -> it.nameVersion)
                                                          .findFirst();

            final List<CodeLocation> uniqueCodeLocations = new ArrayList<>(codeLocationsBySource.values());
            return new Extraction.Builder().success(uniqueCodeLocations).nameVersionIfPresent(nameVersion).build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private NugetTargetResult executeTarget(final NugetInspector inspector, final File targetFile, final File outputDirectory, final NugetInspectorOptions nugetInspectorOptions)
        throws ExecutableRunnerException, IOException, DetectableException {
        if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
            throw new DetectableException(String.format("Executing the nuget inspector failed, could not create output directory: %s", outputDirectory));
        }

        final ExecutableOutput executableOutput = inspector.execute(outputDirectory, targetFile, outputDirectory, nugetInspectorOptions);

        if (executableOutput.getReturnCode() != 0) {
            throw new DetectableException(String.format("Executing the nuget inspector failed: %s", executableOutput.getReturnCode()));
        }

        final List<File> dependencyNodeFiles = fileFinder.findFiles(outputDirectory, INSPECTOR_OUTPUT_PATTERN);

        final List<NugetParseResult> parseResults = new ArrayList<>();
        if (dependencyNodeFiles != null) {
            for (final File dependencyNodeFile : dependencyNodeFiles) {
                final String text = FileUtils.readFileToString(dependencyNodeFile, StandardCharsets.UTF_8);
                final NugetParseResult result = nugetInspectorParser.createCodeLocation(text);
                parseResults.add(result);
            }
        }

        final NugetTargetResult targetResult = new NugetTargetResult();

        targetResult.codeLocations = parseResults.stream()
                                         .flatMap(it -> it.getCodeLocations().stream())
                                         .collect(Collectors.toList());

        targetResult.nameVersion = parseResults.stream()
                                       .filter(it -> StringUtils.isNotBlank(it.getProjectName()))
                                       .map(it -> new NameVersion(it.getProjectName(), it.getProjectVersion()))
                                       .findFirst()
                                       .orElse(null);

        return targetResult;
    }

}
