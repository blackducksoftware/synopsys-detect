/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.swift;

import java.io.File;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.swift.model.SwiftPackage;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class SwiftExtractor {
    private final DetectableExecutableRunner executableRunner;
    private final SwiftCliParser swiftCliParser;
    private final SwiftPackageTransformer swiftPackageTransformer;

    public SwiftExtractor(DetectableExecutableRunner executableRunner, SwiftCliParser swiftCliParser, SwiftPackageTransformer swiftPackageTransformer) {
        this.executableRunner = executableRunner;
        this.swiftCliParser = swiftCliParser;
        this.swiftPackageTransformer = swiftPackageTransformer;
    }

    public Extraction extract(File environmentDirectory, ExecutableTarget swiftExecutable) {
        try {
            SwiftPackage rootSwiftPackage = getRootSwiftPackage(environmentDirectory, swiftExecutable);
            CodeLocation codeLocation = swiftPackageTransformer.transform(rootSwiftPackage);

            return new Extraction.Builder()
                       .success(codeLocation)
                       .projectName(rootSwiftPackage.getName())
                       .projectVersion(rootSwiftPackage.getVersion())
                       .build();
        } catch (IntegrationException | ExecutableRunnerException e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private SwiftPackage getRootSwiftPackage(File environmentDirectory, ExecutableTarget swiftExecutable) throws ExecutableRunnerException, IntegrationException {
        ExecutableOutput executableOutput = executableRunner.execute(ExecutableUtils.createFromTarget(environmentDirectory, swiftExecutable, "package", "show-dependencies", "--format", "json"));
        if (executableOutput.getReturnCode() == 0) {
            return swiftCliParser.parseOutput(executableOutput.getStandardOutputAsList());
        } else {
            throw new IntegrationException(String.format("Swift returned a non-zero exit code (%d). Failed to parse output.", executableOutput.getReturnCode()));
        }
    }

}
