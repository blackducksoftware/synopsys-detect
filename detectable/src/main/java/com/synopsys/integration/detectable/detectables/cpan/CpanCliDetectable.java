/**
 * hub-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.synopsys.integration.detectable.detectables.cpan;

import java.io.File;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableResolver;
import com.synopsys.integration.detectable.detectable.executable.ExecutableType;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;

public class CpanCliDetectable extends Detectable {
    public static final String CPAN_EXECUTABLE_NAME = "cpan";
    public static final String CPANM_EXECUTABLE_NAME = "cpanm";
    public static final String MAKEFILE = "Makefile.PL";

    private final FileFinder fileFinder;
    private final ExecutableResolver executableResolver;
    private final CpanCliExtractor cpanCliExtractor;

    private File cpanExe;
    private File cpanmExe;

    public CpanCliDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final ExecutableResolver executableResolver, final CpanCliExtractor cpanCliExtractor) {
        super(environment, "Cpan Cli", "CPAN");
        this.fileFinder = fileFinder;
        this.cpanCliExtractor = cpanCliExtractor;
        this.executableResolver = executableResolver;
    }

    @Override
    public DetectableResult applicable() {
        final File makeFile = fileFinder.findFile(environment.getDirectory(), MAKEFILE);
        if (makeFile == null) {
            return new FileNotFoundDetectableResult(MAKEFILE);
        }

        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        final File cpan = executableResolver.resolveExecutable(ExecutableType.CPAN, environment);

        if (cpan == null) {
            return new ExecutableNotFoundDetectableResult("cpan");
        } else {
            cpanExe = cpan;
        }

        final File cpanm = executableResolver.resolveExecutable(ExecutableType.CPANM, environment);

        if (cpanm == null) {
            return new ExecutableNotFoundDetectableResult("cpanm");
        } else {
            cpanmExe = cpanm;
        }

        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        return cpanCliExtractor.extract(environment.getDirectory(), cpanExe, cpanmExe, extractionEnvironment.getOutputDirectory());
    }

}
