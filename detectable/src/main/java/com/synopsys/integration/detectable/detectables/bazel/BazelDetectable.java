/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.bazel;

import java.io.File;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.BazelResolver;
import com.synopsys.integration.detectable.detectable.explanation.PropertyProvided;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.FilesNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PropertyInsufficientDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "various", forge = "Maven Central", requirementsMarkdown = "File: WORKSPACE. <br /><br /> Executable: bazel.")
public class BazelDetectable extends Detectable {
    public static final String WORKSPACE_FILENAME = "WORKSPACE";
    private final FileFinder fileFinder;
    private final BazelExtractor bazelExtractor;
    private final BazelResolver bazelResolver;
    private final BazelDetectableOptions bazelDetectableOptions;
    private ExecutableTarget bazelExe;
    private BazelWorkspace bazelWorkspace;

    public BazelDetectable(DetectableEnvironment environment, FileFinder fileFinder, BazelExtractor bazelExtractor,
        BazelResolver bazelResolver, BazelDetectableOptions bazelDetectableOptions) {
        super(environment);
        this.fileFinder = fileFinder;
        this.bazelExtractor = bazelExtractor;
        this.bazelResolver = bazelResolver;
        this.bazelDetectableOptions = bazelDetectableOptions;
    }

    @Override
    public DetectableResult applicable() {
        if (bazelDetectableOptions.getTargetName().isPresent()) {
            return new PassedDetectableResult(new PropertyProvided("Bazel Target"));
        } else {
            return new PropertyInsufficientDetectableResult();
        }
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        Requirements requirements = new Requirements(fileFinder, environment);
        File workspaceFile = requirements.file(WORKSPACE_FILENAME);

        requirements.ifCurrentlyMet(() -> {
            bazelWorkspace = new BazelWorkspace(workspaceFile);
        });

        bazelExe = requirements.executable(bazelResolver::resolveBazel, "bazel");
        return requirements.result();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        BazelProjectNameGenerator projectNameGenerator = new BazelProjectNameGenerator();
        // Checked in applicable.
        return bazelExtractor
                   .extract(bazelExe, environment.getDirectory(), bazelWorkspace, bazelDetectableOptions.getTargetName().get(), projectNameGenerator, bazelDetectableOptions.getBazelDependencyRules(),
                       bazelDetectableOptions.getBazelCqueryAdditionalOptions());
    }
}
