package com.synopsys.integration.detectable.detectables.pip.inspector;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.pip.inspector.model.NameVersionCodeLocation;
import com.synopsys.integration.detectable.detectables.pip.inspector.parser.PipInspectorTreeParser;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.util.ToolVersionLogger;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class PipInspectorExtractor {
    private final DetectableExecutableRunner executableRunner;
    private final PipInspectorTreeParser pipInspectorTreeParser;
    private final ToolVersionLogger toolVersionLogger;

    public PipInspectorExtractor(DetectableExecutableRunner executableRunner, PipInspectorTreeParser pipInspectorTreeParser, ToolVersionLogger toolVersionLogger) {
        this.executableRunner = executableRunner;
        this.pipInspectorTreeParser = pipInspectorTreeParser;
        this.toolVersionLogger = toolVersionLogger;
    }

    public Extraction extract(
        File directory,
        ExecutableTarget pythonExe,
        ExecutableTarget pipExe,
        File pipInspector,
        File setupFile,
        List<Path> requirementFilePaths,
        String providedProjectName
    ) {
        toolVersionLogger.log(directory, pythonExe);
        toolVersionLogger.log(directory, pipExe);
        Extraction extractionResult;
        try {
            String projectName = getProjectName(directory, pythonExe, setupFile, providedProjectName);
            List<CodeLocation> codeLocations = new ArrayList<>();
            String projectVersion = null;

            List<Path> requirementsPaths = new ArrayList<>();

            if (requirementFilePaths.isEmpty()) {
                requirementsPaths.add(null);
            } else {
                requirementsPaths.addAll(requirementFilePaths);
            }

            for (Path requirementFilePath : requirementsPaths) {
                List<String> inspectorOutput = runInspector(directory, pythonExe, pipInspector, projectName, requirementFilePath);
                Optional<NameVersionCodeLocation> result = pipInspectorTreeParser.parse(inspectorOutput, directory.toString());
                if (result.isPresent()) {
                    codeLocations.add(result.get().getCodeLocation());
                    String potentialProjectVersion = result.get().getProjectVersion();
                    if (projectVersion == null && StringUtils.isNotBlank(potentialProjectVersion)) {
                        projectVersion = potentialProjectVersion;
                    }
                }
            }

            if (codeLocations.isEmpty()) {
                extractionResult = new Extraction.Builder().failure("The Pip Inspector tree parse failed to produce output.").build();
            } else {
                extractionResult = new Extraction.Builder()
                    .success(codeLocations)
                    .projectName(projectName)
                    .projectVersion(projectVersion)
                    .build();
            }
        } catch (Exception e) {
            extractionResult = new Extraction.Builder().exception(e).build();
        }

        return extractionResult;
    }

    private List<String> runInspector(File sourceDirectory, ExecutableTarget pythonExe, File inspectorScript, String projectName, Path requirementsFilePath)
        throws ExecutableRunnerException {
        List<String> inspectorArguments = new ArrayList<>();
        inspectorArguments.add(inspectorScript.getAbsolutePath());

        if (requirementsFilePath != null) {
            inspectorArguments.add(String.format("--requirements=%s", requirementsFilePath.toAbsolutePath().toString()));
        }

        if (StringUtils.isNotBlank(projectName)) {
            inspectorArguments.add(String.format("--projectname=%s", projectName));
        }

        return executableRunner.execute(ExecutableUtils.createFromTarget(sourceDirectory, pythonExe, inspectorArguments)).getStandardOutputAsList();
    }

    private String getProjectName(File directory, ExecutableTarget pythonExe, File setupFile, String providedProjectName) throws ExecutableRunnerException {
        String projectName = providedProjectName;

        if (StringUtils.isBlank(projectName) && setupFile != null && setupFile.exists()) {
            List<String> pythonArguments = Arrays.asList(setupFile.getAbsolutePath(), "--name");
            List<String> output = executableRunner.execute(ExecutableUtils.createFromTarget(directory, pythonExe, pythonArguments)).getStandardOutputAsList();
            projectName = output.get(output.size() - 1).replace('_', '-').trim();
        }

        return projectName;
    }
}
