/**
 * detectable
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
package com.synopsys.integration.detectable.detectables.clang;

import java.io.File;
import java.util.List;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManager;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerRunner;
import com.synopsys.integration.exception.IntegrationException;

public class ClangDetectable extends Detectable {
    private static final String JSON_COMPILATION_DATABASE_FILENAME = "compile_commands.json";
    private final ClangExtractor clangExtractor;
    private final ClangDetectableOptions options;
    private File jsonCompilationDatabaseFile = null;
    private final FileFinder fileFinder;
    private final ExecutableRunner executableRunner;
    private final List<ClangPackageManager> availablePackageManagers;
    private final ClangPackageManagerRunner packageManagerRunner;

    private ClangPackageManager selectedPackageManager;

    public ClangDetectable(final DetectableEnvironment environment, final ExecutableRunner executableRunner, final FileFinder fileFinder, final List<ClangPackageManager> availablePackageManagers, final ClangExtractor clangExtractor,
        ClangDetectableOptions options, final ClangPackageManagerRunner packageManagerRunner) {
        super(environment, "Clang", "Clang");
        this.fileFinder = fileFinder;
        this.availablePackageManagers = availablePackageManagers;
        this.executableRunner = executableRunner;
        this.clangExtractor = clangExtractor;
        this.options = options;
        this.packageManagerRunner = packageManagerRunner;
    }

    @Override
    public DetectableResult applicable() {
        jsonCompilationDatabaseFile = fileFinder.findFile(environment.getDirectory(), JSON_COMPILATION_DATABASE_FILENAME);
        if (jsonCompilationDatabaseFile == null) {
            return new FileNotFoundDetectableResult(JSON_COMPILATION_DATABASE_FILENAME);
        }
        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        try {
            selectedPackageManager = findPkgMgr(environment.getDirectory());
        } catch (final IntegrationException e) {
            return new ExecutableNotFoundDetectableResult("supported Linux package manager");
        }
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        //addRelevantDiagnosticFile(jsonCompilationDatabaseFile);
        return clangExtractor.extract(selectedPackageManager, packageManagerRunner, environment.getDirectory(), options.getDepth(), extractionEnvironment.getOutputDirectory(), jsonCompilationDatabaseFile, options.isCleanup());
    }

    private ClangPackageManager findPkgMgr(File workingDirectory) throws IntegrationException {
        for (final ClangPackageManager pkgMgrCandidate : availablePackageManagers) {
            if (packageManagerRunner.applies(pkgMgrCandidate, workingDirectory, executableRunner)) {
                return pkgMgrCandidate;
            }
        }
        throw new IntegrationException("Unable to execute any supported package manager; Please make sure that one of the supported package managers is on the PATH");
    }
}
