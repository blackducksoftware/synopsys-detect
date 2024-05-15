package com.synopsys.integration.detectable.detectables.nuget;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
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

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.nuget.parse.NugetInspectorParser;
import com.synopsys.integration.detectable.detectables.nuget.parse.NugetParseResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.executable.Executable;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;
import com.synopsys.integration.util.NameVersion;

public class NugetInspectorExtractor {
    public static final String INSPECTOR_OUTPUT_PATTERN = "*_inspection.json";

    private final Logger logger = LoggerFactory.getLogger(NugetInspectorExtractor.class);

    private final NugetInspectorParser nugetInspectorParser;
    private final FileFinder fileFinder;
    private final DetectableExecutableRunner executableRunner;

    public NugetInspectorExtractor(NugetInspectorParser nugetInspectorParser, FileFinder fileFinder, DetectableExecutableRunner executableRunner) {
        this.nugetInspectorParser = nugetInspectorParser;
        this.fileFinder = fileFinder;
        this.executableRunner = executableRunner;
    }

    public Extraction extract(List<File> targets, File outputDirectory, ExecutableTarget inspector, NugetInspectorOptions nugetInspectorOptions) {
        try {
            String targetFilesPath = outputDirectory.getParentFile().getParentFile().getCanonicalPath().concat("/targetPaths.tmp");
            writeTargetPathsToTemporaryLockedFile(targetFilesPath, targets);
            NugetTargetResult result = executeTarget(inspector, targetFilesPath, outputDirectory, nugetInspectorOptions);

            List<CodeLocation> codeLocations = result.codeLocations.stream().collect(Collectors.toList());

            Map<File, CodeLocation> codeLocationsBySource = new HashMap<>();

            codeLocations.forEach(codeLocation -> {
                File sourcePathFile = codeLocation.getSourcePath().orElse(null);
                if (codeLocationsBySource.containsKey(sourcePathFile)) {
                    logger.debug("Combined code location for: {}", sourcePathFile);
                    CodeLocation destination = codeLocationsBySource.get(sourcePathFile);
                    destination.getDependencyGraph().copyGraphToRoot(codeLocation.getDependencyGraph());
                } else {
                    codeLocationsBySource.put(sourcePathFile, codeLocation);
                }
            });

            Optional<NameVersion> nameVersion = Optional.of(result.nameVersion);

            List<CodeLocation> uniqueCodeLocations = new ArrayList<>(codeLocationsBySource.values());
            return new Extraction.Builder().success(uniqueCodeLocations).nameVersionIfPresent(nameVersion).build();
        } catch (DetectableException | ExecutableRunnerException | IOException e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private NugetTargetResult executeTarget(ExecutableTarget inspector, String targetFiles, File outputDirectory, NugetInspectorOptions nugetInspectorOptions)
        throws ExecutableRunnerException, IOException, DetectableException {
        if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
            throw new DetectableException(String.format("Executing the nuget inspector failed, could not create output directory: %s", outputDirectory));
        }

        List<String> arguments = NugetInspectorArguments.fromInspectorOptions(nugetInspectorOptions, targetFiles, outputDirectory);
        Executable executable = ExecutableUtils.createFromTarget(outputDirectory, inspector, arguments);
        ExecutableOutput executableOutput = executableRunner.execute(executable);

        if (executableOutput.getReturnCode() != 0) {
            throw new DetectableException(String.format("Executing the nuget inspector failed: %s", executableOutput.getReturnCode()));
        }

        List<File> dependencyNodeFiles = fileFinder.findFiles(outputDirectory, INSPECTOR_OUTPUT_PATTERN);

        List<NugetParseResult> parseResults = new ArrayList<>();
        for (File dependencyNodeFile : dependencyNodeFiles) {
            String text = FileUtils.readFileToString(dependencyNodeFile, StandardCharsets.UTF_8);
            NugetParseResult result = nugetInspectorParser.createCodeLocation(text);
            parseResults.add(result);
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
    
    public void writeTargetPathsToTemporaryLockedFile(String filePath, List<File> targets) throws IOException {
        
        FileChannel channel;
        try (RandomAccessFile stream = new RandomAccessFile(filePath, "rw")) {
            channel = stream.getChannel();
            FileLock lock = null;
            try {
                lock = channel.tryLock();
            } catch (final OverlappingFileLockException e) {
                stream.close();
                channel.close();
            }
            for (File target : targets) {
                stream.writeChars(target.getAbsolutePath());
                stream.writeChars(System.lineSeparator());
            }
            if (lock != null) {
                lock.release();
            }
        }
        channel.close();
    }
}
