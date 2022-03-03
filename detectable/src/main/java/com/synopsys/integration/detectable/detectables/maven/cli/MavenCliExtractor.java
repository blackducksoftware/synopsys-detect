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
        List<String> commandArguments = commandParser.parseCommandString(mavenCliExtractorOptions.getMavenBuildCommand().orElse("")).stream()
            .filter(arg -> !arg.equals("dependency:tree"))
            .collect(Collectors.toList());

        commandArguments.add("dependency:tree");
        commandArguments.add("-T1"); // Force maven to use a single thread to ensure the tree output is in the correct order.

        ExecutableOutput mvnExecutableResult = executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(directory, mavenExe, commandArguments));

        List<String> mavenOutput = mvnExecutableResult.getStandardOutputAsList();
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

        Optional<MavenParseResult> firstWithName = Bds.of(mavenResults)
            .firstFiltered(it -> StringUtils.isNotBlank(it.getProjectName()));

        Extraction.Builder builder = new Extraction.Builder().success(codeLocations);
        if (firstWithName.isPresent()) {
            builder.projectName(firstWithName.get().getProjectName());
            builder.projectVersion(firstWithName.get().getProjectVersion());
        }
        return builder.build();
    }

}
