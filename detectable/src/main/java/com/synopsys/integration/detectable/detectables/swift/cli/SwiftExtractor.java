package com.synopsys.integration.detectable.detectables.swift.cli;

import java.io.File;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectables.swift.cli.model.SwiftPackage;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.util.ToolVersionLogger;
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
