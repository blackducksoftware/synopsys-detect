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
package com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver;

import java.io.File;
import java.util.List;
import java.util.Optional;

import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerInfo;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class ApkArchitectureResolver {
    private Optional<String> architecture = Optional.empty();
    private boolean hasAttemptedResolution = false;

    public Optional<String> resolveArchitecture(ClangPackageManagerInfo currentPackageManager, File workingDirectory, DetectableExecutableRunner executableRunner) throws ExecutableRunnerException {
        if (hasAttemptedResolution) {
            return architecture;
        }

        hasAttemptedResolution = true;
        if (currentPackageManager.getPkgArchitectureArgs().isPresent()) {
            List<String> args = currentPackageManager.getPkgArchitectureArgs().get();
            String cmd = currentPackageManager.getPkgMgrCmdString();
            ExecutableOutput architectureOutput = executableRunner.execute(workingDirectory, cmd, args);
            architecture = Optional.ofNullable(architectureOutput.getStandardOutput().trim());
        } else {
            architecture = Optional.empty();
        }
        return architecture;
    }
}
