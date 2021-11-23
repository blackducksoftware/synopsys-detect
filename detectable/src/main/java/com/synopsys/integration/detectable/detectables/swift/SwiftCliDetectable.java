/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.swift;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.SwiftResolver;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "Swift", forge = "Swift.org", requirementsMarkdown = "File: Package.swift. Executables: swift.")
public class SwiftCliDetectable extends Detectable {
    private static final String PACKAGE_SWIFT_FILENAME = "Package.swift";

    private final FileFinder fileFinder;
    private final SwiftExtractor swiftExtractor;
    private final SwiftResolver swiftResolver;

    private ExecutableTarget swiftExecutable;

    public SwiftCliDetectable(DetectableEnvironment environment, FileFinder fileFinder, SwiftExtractor swiftExtractor, SwiftResolver swiftResolver) {
        super(environment);
        this.fileFinder = fileFinder;
        this.swiftExtractor = swiftExtractor;
        this.swiftResolver = swiftResolver;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        requirements.file(PACKAGE_SWIFT_FILENAME);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        Requirements requirements = new Requirements(fileFinder, environment);
        swiftExecutable = requirements.executable(swiftResolver::resolveSwift, "swift");
        return requirements.result();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return swiftExtractor.extract(environment.getDirectory(), swiftExecutable);
    }

}
