package com.synopsys.integration.detectable.detectables.maven.cli;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.common.util.parse.CommandParser;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.util.ToolVersionLogger;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.util.NameVersion;

public class MavenCliExtractor {
    private final DetectableExecutableRunner executableRunner;
    private final MavenCodeLocationPackager mavenCodeLocationPackager;
    private final CommandParser commandParser;
    private final ToolVersionLogger toolVersionLogger;

    public MavenCliExtractor(
        DetectableExecutableRunner executableRunner,
        MavenCodeLocationPackager mavenCodeLocationPackager,
        CommandParser commandParser,
        ToolVersionLogger toolVersionLogger
    ) {
        this.executableRunner = executableRunner;
        this.mavenCodeLocationPackager = mavenCodeLocationPackager;
        this.commandParser = commandParser;
        this.toolVersionLogger = toolVersionLogger;
    }

    //TODO: Limit 'extractors' to 'execute' and 'read', delegate all other work.
    public Extraction extract(File directory, ExecutableTarget mavenExe, MavenCliExtractorOptions mavenCliExtractorOptions) throws ExecutableFailedException {
        toolVersionLogger.log(directory, mavenExe);
        //TODO- look at extracting command parsing and command running into a (tested) 'MavenCommandRunner' that takes in a CommandParser and ExecutableRunner
        List<String> commandArguments = commandParser.parseCommandString(mavenCliExtractorOptions.getMavenBuildCommand().orElse("")).stream()
            .filter(arg -> !arg.equals("dependency:tree"))
            .collect(Collectors.toList());

        commandArguments.add("dependency:tree");
        commandArguments.add("-T1"); // Force maven to use a single thread to ensure the tree output is in the correct order.

        ExecutableOutput mvnExecutableResult = executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(directory, mavenExe, commandArguments));

        List<String> mavenOutput = mvnExecutableResult.getStandardOutputAsList();
        //TODO- these options should be passed into constructor of MavenCodeLocationPackager
        List<String> excludedScopes = mavenCliExtractorOptions.getMavenExcludedScopes();
        List<String> includedScopes = mavenCliExtractorOptions.getMavenIncludedScopes();
        List<String> excludedModules = mavenCliExtractorOptions.getMavenExcludedModules();
        List<String> includedModules = mavenCliExtractorOptions.getMavenIncludedModules();
        List<MavenParseResult> mavenResults = mavenCodeLocationPackager.extractCodeLocations(
            directory.toString(),
            mavenOutput,
            excludedScopes,
            includedScopes,
            excludedModules,
            includedModules
        );

        List<CodeLocation> codeLocations = Bds.of(mavenResults)
            .map(MavenParseResult::getCodeLocation)
            .toList();

        Optional<NameVersion> projectNameVersion = Bds.of(mavenResults)
            .firstFiltered(it -> StringUtils.isNotBlank(it.getProjectName()))
            .map(result -> new NameVersion(result.getProjectName(), result.getProjectVersion()));

        return new Extraction.Builder()
            .nameVersionIfPresent(projectNameVersion)
            .success(codeLocations)
            .build();
    }

}
