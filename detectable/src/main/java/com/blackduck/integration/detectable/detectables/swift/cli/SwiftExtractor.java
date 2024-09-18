package com.blackduck.integration.detectable.detectables.swift.cli;

import java.io.File;

import com.blackduck.integration.detectable.detectables.swift.cli.model.SwiftPackage;
import com.blackduck.integration.detectable.ExecutableTarget;
import com.blackduck.integration.detectable.ExecutableUtils;
import com.blackduck.integration.detectable.detectable.codelocation.CodeLocation;
import com.blackduck.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.blackduck.integration.detectable.detectable.executable.ExecutableFailedException;
import com.blackduck.integration.detectable.extraction.Extraction;
import com.blackduck.integration.detectable.util.ToolVersionLogger;
import com.synopsys.integration.executable.ExecutableOutput;

public class SwiftExtractor {
    private final DetectableExecutableRunner executableRunner;
    private final SwiftCliParser swiftCliParser;
    private final SwiftPackageTransformer swiftPackageTransformer;
    private final ToolVersionLogger toolVersionLogger;

    public SwiftExtractor(
        DetectableExecutableRunner executableRunner,
        SwiftCliParser swiftCliParser,
        SwiftPackageTransformer swiftPackageTransformer,
        ToolVersionLogger toolVersionLogger
    ) {
        this.executableRunner = executableRunner;
        this.swiftCliParser = swiftCliParser;
        this.swiftPackageTransformer = swiftPackageTransformer;
        this.toolVersionLogger = toolVersionLogger;
    }

    public Extraction extract(File environmentDirectory, ExecutableTarget swiftExecutable) throws ExecutableFailedException {
        toolVersionLogger.log(environmentDirectory, swiftExecutable);
        SwiftPackage rootSwiftPackage = getRootSwiftPackage(environmentDirectory, swiftExecutable);
        CodeLocation codeLocation = swiftPackageTransformer.transform(rootSwiftPackage);

        return new Extraction.Builder()
            .success(codeLocation)
            .projectName(rootSwiftPackage.getName())
            .projectVersion(rootSwiftPackage.getVersion())
            .build();
    }

    private SwiftPackage getRootSwiftPackage(File environmentDirectory, ExecutableTarget swiftExecutable) throws ExecutableFailedException {
        ExecutableOutput executableOutput = executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(
            environmentDirectory,
            swiftExecutable,
            "package",
            "show-dependencies",
            "--format",
            "json"
        ));
        return swiftCliParser.parseOutput(executableOutput.getStandardOutputAsList());
    }
}
