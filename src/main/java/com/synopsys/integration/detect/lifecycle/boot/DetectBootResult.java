/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detect.lifecycle.boot;

import java.io.File;
import java.util.Optional;

import com.synopsys.integration.detect.config.DetectConfig;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;

public class DetectBootResult {
    private final BootType bootType;

    //No matter what type of boot occurs, the following should be provided to the best effort of the Boot.
    //Shutdown and cleanup will do best effort with the following dependencies.
    private final DetectConfig detectConfiguration;
    private final DirectoryManager directoryManager;
    private final ProductRunData productRunData;
    private final File airGapZip;
    private final DiagnosticSystem diagnosticSystem;

    //And in the case of an exception, this should be populated so the proper exit code can be thrown.
    private final Exception exception;

    private DetectBootResult(final BootType bootType, final DetectConfig detectConfiguration, final DirectoryManager directoryManager, final File airGapZip, final DiagnosticSystem diagnosticSystem,
        final ProductRunData productRunData, final Exception exception) {
        this.bootType = bootType;
        this.detectConfiguration = detectConfiguration;
        this.directoryManager = directoryManager;
        this.airGapZip = airGapZip;
        this.diagnosticSystem = diagnosticSystem;
        this.productRunData = productRunData;
        this.exception = exception;
    }

    public Optional<DetectConfig> getDetectConfiguration() {
        return Optional.ofNullable(detectConfiguration);
    }

    public Optional<DirectoryManager> getDirectoryManager() {
        return Optional.ofNullable(directoryManager);
    }

    public Optional<DiagnosticSystem> getDiagnosticSystem() {
        return Optional.ofNullable(diagnosticSystem);
    }

    public Optional<ProductRunData> getProductRunData() {
        return Optional.ofNullable(productRunData);
    }

    public Optional<File> getAirGapZip() {
        return Optional.ofNullable(airGapZip);
    }

    public BootType getBootType() {
        return bootType;
    }

    public Optional<Exception> getException() {
        return Optional.ofNullable(exception);
    }

    public enum BootType {
        EXIT,
        RUN,
        EXCEPTION
    }

    public static DetectBootResult run(final DetectConfig detectConfiguration, final ProductRunData productRunData, final DirectoryManager directoryManager, final Optional<DiagnosticSystem> diagnosticSystem) {
        return new DetectBootResult(BootType.RUN, detectConfiguration, directoryManager, null, diagnosticSystem.orElse(null), productRunData, null);
    }

    public static DetectBootResult exit(final DetectConfig detectConfiguration) {
        return new DetectBootResult(BootType.EXIT, detectConfiguration, null, null, null, null, null);
    }

    public static DetectBootResult exit(final DetectConfig detectConfiguration, final DirectoryManager directoryManager, final Optional<DiagnosticSystem> diagnosticSystem) {
        return new DetectBootResult(BootType.EXIT, detectConfiguration, directoryManager, null, diagnosticSystem.orElse(null), null, null);
    }

    public static DetectBootResult exit(final DetectConfig detectConfiguration, final File airGapZip, final DirectoryManager directoryManager, final Optional<DiagnosticSystem> diagnosticSystem) {
        return new DetectBootResult(BootType.EXIT, detectConfiguration, directoryManager, airGapZip, diagnosticSystem.orElse(null), null, null);
    }

    public static DetectBootResult exception(final Exception exception, final DetectConfig detectConfiguration) {
        return new DetectBootResult(BootType.EXCEPTION, detectConfiguration, null, null, null, null, exception);
    }

    public static DetectBootResult exception(final Exception exception, final DetectConfig detectConfiguration, final DirectoryManager directoryManager, final Optional<DiagnosticSystem> diagnosticSystem) {
        return new DetectBootResult(BootType.EXCEPTION, detectConfiguration, directoryManager, null, diagnosticSystem.orElse(null), null, exception);
    }
}
