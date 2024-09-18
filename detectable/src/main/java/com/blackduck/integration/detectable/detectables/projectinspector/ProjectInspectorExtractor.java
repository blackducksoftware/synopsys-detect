package com.blackduck.integration.detectable.detectables.projectinspector;

import java.io.File;
import java.util.*;

import com.blackduck.integration.detectable.ExecutableTarget;
import com.blackduck.integration.detectable.ExecutableUtils;
import com.blackduck.integration.detectable.detectable.codelocation.CodeLocation;
import com.blackduck.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.blackduck.integration.detectable.detectable.executable.ExecutableFailedException;
import com.blackduck.integration.detectable.extraction.Extraction;

public class ProjectInspectorExtractor {

    private static final String INCLUDE_SHADED_DEPENDENCIES = "include_shaded_dependencies";
    private final DetectableExecutableRunner executableRunner;
    private final ProjectInspectorParser projectInspectorParser;

    public ProjectInspectorExtractor(DetectableExecutableRunner executableRunner, ProjectInspectorParser projectInspectorParser) {
        this.executableRunner = executableRunner;
        this.projectInspectorParser = projectInspectorParser;
    }

    public Extraction extract(
        ProjectInspectorOptions projectInspectorOptions, // TODO: Take in needed values through constructor
        List<String> extra,
        File targetDirectory,
        File outputDirectory,
        ExecutableTarget inspector
    ) throws ExecutableFailedException {
        File outputFile = new File(outputDirectory, "inspection.json");
        boolean includeShadedDependencies = false;

        if(extra.contains(INCLUDE_SHADED_DEPENDENCIES)) {
            includeShadedDependencies = true;
            extra = Collections.emptyList();
        }

        // TODO: Could use a command runner
        List<String> arguments = new LinkedList<>();
        Optional.ofNullable(projectInspectorOptions.getGlobalArguments())
                .map(arg -> arg.split(" "))
                .ifPresent(globalArguments -> arguments.addAll(Arrays.asList(globalArguments)));
        arguments.add("inspect");
        arguments.add("--dir");
        arguments.add(targetDirectory.toString());
        arguments.add("--output-file");
        arguments.add(outputFile.toString());
        arguments.addAll(extra);

        Optional.ofNullable(projectInspectorOptions.getAdditionalArguments())
            .map(arg -> arg.split(" "))
            .ifPresent(additionalArguments -> arguments.addAll(Arrays.asList(additionalArguments)));

        executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(targetDirectory, inspector, arguments));
        List<CodeLocation> codeLocations;
        try {
            codeLocations = projectInspectorParser.parse(outputFile, includeShadedDependencies);
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }

        return new Extraction.Builder().success(codeLocations).build();
    }

    public Map<String, Set<String>> getShadedDependencies() {
        return projectInspectorParser.getShadedDependencies();
    }
}
