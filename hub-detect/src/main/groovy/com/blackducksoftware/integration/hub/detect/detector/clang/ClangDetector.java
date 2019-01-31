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
package com.blackducksoftware.integration.hub.detect.detector.clang;

import java.io.File;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.detector.Detector;
import com.blackducksoftware.integration.hub.detect.detector.DetectorEnvironment;
import com.blackducksoftware.integration.hub.detect.detector.DetectorException;
import com.blackducksoftware.integration.hub.detect.detector.DetectorType;
import com.blackducksoftware.integration.hub.detect.detector.ExtractionId;
import com.blackducksoftware.integration.hub.detect.detector.clang.packagemanager.ClangPackageManager;
import com.blackducksoftware.integration.hub.detect.detector.clang.packagemanager.ClangPackageManagerRunner;
import com.blackducksoftware.integration.hub.detect.detector.clang.packagemanager.ClangPackageManagerInfo;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.ExecutableNotFoundDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.FileNotFoundDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.PassedDetectorResult;
import com.synopsys.integration.exception.IntegrationException;

public class ClangDetector extends Detector {
    private static final String JSON_COMPILATION_DATABASE_FILENAME = "compile_commands.json";
    private final ClangExtractor clangExtractor;
    private final boolean cleanup;
    private File jsonCompilationDatabaseFile = null;
    private final DetectFileFinder fileFinder;
    private final ExecutableRunner executableRunner;
    private final List<ClangPackageManager> availablePackageManagers;
    private final ClangPackageManagerRunner packageManagerRunner;

    private ClangPackageManager selectedPackageManager;

    public ClangDetector(final DetectorEnvironment environment, final ExecutableRunner executableRunner, final DetectFileFinder fileFinder, final List<ClangPackageManager> availablePackageManagers, final ClangExtractor clangExtractor,
        boolean cleanup, final ClangPackageManagerRunner packageManagerRunner) {
        super(environment, "Clang", DetectorType.CLANG);
        this.fileFinder = fileFinder;
        this.availablePackageManagers = availablePackageManagers;
        this.executableRunner = executableRunner;
        this.clangExtractor = clangExtractor;
        this.cleanup = cleanup;
        this.packageManagerRunner = packageManagerRunner;
    }

    @Override
    public DetectorResult applicable() {
        jsonCompilationDatabaseFile = fileFinder.findFile(environment.getDirectory(), JSON_COMPILATION_DATABASE_FILENAME);
        if (jsonCompilationDatabaseFile == null) {
            return new FileNotFoundDetectorResult(JSON_COMPILATION_DATABASE_FILENAME);
        }
        return new PassedDetectorResult();
    }

    @Override
    public DetectorResult extractable() throws DetectorException {
        try {
            selectedPackageManager = findPkgMgr(environment.getDirectory());
        } catch (final IntegrationException e) {
            return new ExecutableNotFoundDetectorResult("supported Linux package manager");
        }
        return new PassedDetectorResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        addRelevantDiagnosticFile(jsonCompilationDatabaseFile);
        return clangExtractor.extract(selectedPackageManager, packageManagerRunner, environment.getDirectory(), environment.getDepth(), extractionId, jsonCompilationDatabaseFile, cleanup);
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
