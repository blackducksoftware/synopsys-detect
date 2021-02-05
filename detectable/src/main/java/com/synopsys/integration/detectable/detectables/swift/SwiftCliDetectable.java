/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detectable.detectables.swift;

import java.io.File;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.SwiftResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "Swift", forge = "Swift.org", requirementsMarkdown = "File: Package.swift.<br/><br/> Executables: swift.")
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
        File foundPackageSwift = fileFinder.findFile(environment.getDirectory(), PACKAGE_SWIFT_FILENAME);
        if (foundPackageSwift == null) {
            return new FileNotFoundDetectableResult(PACKAGE_SWIFT_FILENAME);
        }

        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        swiftExecutable = swiftResolver.resolveSwift();
        if (swiftExecutable == null) {
            return new ExecutableNotFoundDetectableResult("swift");
        }

        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return swiftExtractor.extract(environment.getDirectory(), swiftExecutable);
    }

}
