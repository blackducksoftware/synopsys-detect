/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.maven.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.executable.ExecutableOutput;

public class MavenCliExtractor {
    private final DetectableExecutableRunner executableRunner;
    private final MavenCodeLocationPackager mavenCodeLocationPackager;

    public MavenCliExtractor(DetectableExecutableRunner executableRunner, MavenCodeLocationPackager mavenCodeLocationPackager) {
        this.executableRunner = executableRunner;
        this.mavenCodeLocationPackager = mavenCodeLocationPackager;
    }

    //TODO: Limit 'extractors' to 'execute' and 'read', delegate all other work.
    public Extraction extract(File directory, ExecutableTarget mavenExe, MavenCliExtractorOptions mavenCliExtractorOptions) throws ExecutableFailedException {
        String[] mavenCommand = mavenCliExtractorOptions.getMavenBuildCommand()
                                    .map(cmd -> cmd.replace("dependency:tree", ""))
                                    .map(String::trim)
                                    .map(cmd -> cmd.split(" "))
                                    .orElse(new String[] {});

        List<String> arguments = new ArrayList<>(Arrays.asList(mavenCommand));
        arguments.add("dependency:tree");
        arguments.add("-T1"); // Force maven to use a single thread to ensure the tree output is in the correct order.

        ExecutableOutput mvnExecutableResult = executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(directory, mavenExe, arguments));

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
