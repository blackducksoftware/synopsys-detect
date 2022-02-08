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
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
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

    public NugetInspectorExtractor(NugetInspectorParser nugetInspectorParser, FileFinder fileFinder) {
        this.nugetInspectorParser = nugetInspectorParser;
        this.fileFinder = fileFinder;
    }

    public Extraction extract(List<File> targets, File outputDirectory, NugetInspector inspector, NugetInspectorOptions nugetInspectorOptions) {
        try {
            List<NugetTargetResult> results = new ArrayList<>();

            for (int i = 0; i < targets.size(); i++) {
                File targetDirectory = new File(outputDirectory, "inspection-" + i);
                results.add(executeTarget(inspector, targets.get(i), targetDirectory, nugetInspectorOptions));
            }

            List<CodeLocation> codeLocations = results.stream()
                .flatMap(it -> it.codeLocations.stream())
                .collect(Collectors.toList());

            Map<File, CodeLocation> codeLocationsBySource = new HashMap<>();
            DependencyGraphCombiner combiner = new DependencyGraphCombiner();

            codeLocations.forEach(codeLocation -> {
                File sourcePathFile = codeLocation.getSourcePath().orElse(null);
                if (codeLocationsBySource.containsKey(sourcePathFile)) {
                    logger.debug("Combined code location for: " + sourcePathFile);
                    CodeLocation destination = codeLocationsBySource.get(sourcePathFile);
                    combiner.addGraphAsChildrenToRoot((MutableDependencyGraph) destination.getDependencyGraph(), codeLocation.getDependencyGraph());
                } else {
                    codeLocationsBySource.put(sourcePathFile, codeLocation);
                }
            });

            Optional<NameVersion> nameVersion = results.stream()
                .filter(it -> it.nameVersion != null)
                .map(it -> it.nameVersion)
                .findFirst();

            List<CodeLocation> uniqueCodeLocations = new ArrayList<>(codeLocationsBySource.values());
            return new Extraction.Builder().success(uniqueCodeLocations).nameVersionIfPresent(nameVersion).build();
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private NugetTargetResult executeTarget(NugetInspector inspector, File targetFile, File outputDirectory, NugetInspectorOptions nugetInspectorOptions)
        throws ExecutableRunnerException, IOException, DetectableException {
        if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
            throw new DetectableException(String.format("Executing the nuget inspector failed, could not create output directory: %s", outputDirectory));
        }

        ExecutableOutput executableOutput = inspector.execute(outputDirectory, targetFile, outputDirectory, nugetInspectorOptions);

        if (executableOutput.getReturnCode() != 0) {
            throw new DetectableException(String.format("Executing the nuget inspector failed: %s", executableOutput.getReturnCode()));
        }

        List<File> dependencyNodeFiles = fileFinder.findFiles(outputDirectory, INSPECTOR_OUTPUT_PATTERN);

        List<NugetParseResult> parseResults = new ArrayList<>();
        if (dependencyNodeFiles != null) {
            for (File dependencyNodeFile : dependencyNodeFiles) {
                String text = FileUtils.readFileToString(dependencyNodeFile, StandardCharsets.UTF_8);
                NugetParseResult result = nugetInspectorParser.createCodeLocation(text);
                parseResults.add(result);
            }
        }

        NugetTargetResult targetResult = new NugetTargetResult();

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
