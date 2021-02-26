/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.maven.cli;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectables.clang.compilecommand.ArgumentParser;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.executable.ExecutableOutput;

public class MavenCliExtractor {
    private final DetectableExecutableRunner executableRunner;
    private final MavenCodeLocationPackager mavenCodeLocationPackager;
    private final ArgumentParser argumentParser;

    public MavenCliExtractor(DetectableExecutableRunner executableRunner, MavenCodeLocationPackager mavenCodeLocationPackager, ArgumentParser argumentParser) {
        this.executableRunner = executableRunner;
        this.mavenCodeLocationPackager = mavenCodeLocationPackager;
        this.argumentParser = argumentParser;
    }

    //TODO: Limit 'extractors' to 'execute' and 'read', delegate all other work.
    public Extraction extract(File directory, ExecutableTarget mavenExe, MavenCliExtractorOptions mavenCliExtractorOptions) throws ExecutableFailedException {

        List<String> commandArguments = argumentParser.parseCommandString(mavenCliExtractorOptions.getMavenBuildCommand().orElse(""), new HashMap<>()).stream()
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
        List<MavenParseResult> mavenResults = mavenCodeLocationPackager.extractCodeLocations(directory.toString(), mavenOutput, excludedScopes, includedScopes, excludedModules, includedModules);

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
