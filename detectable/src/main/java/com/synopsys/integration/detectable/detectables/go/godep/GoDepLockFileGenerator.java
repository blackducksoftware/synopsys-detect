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
package com.synopsys.integration.detectable.detectables.go.godep;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;

public class GoDepLockFileGenerator {
    private final Logger logger = LoggerFactory.getLogger(GoDepLockFileGenerator.class);

    private final ExecutableRunner executableRunner;

    public GoDepLockFileGenerator(final ExecutableRunner executableRunner) {
        this.executableRunner = executableRunner;
    }

    public Optional<File> findOrMakeLockFile(final File location, final File goDepExecutable, boolean allowsRunInit) throws IOException {
        final File lockFile = new File(location, "Gopkg.lock");

        if (lockFile.exists()) {
            return Optional.of(lockFile);
        }

        if (allowsRunInit){
            createGoPkgLockWithInitAndEnsure(location, goDepExecutable);
        } else {
            logger.info("Will not attempt Dep commands 'init' and 'ensure'");
        }

        if (lockFile.exists()){
            return Optional.of(lockFile);
        } else {
            return Optional.empty();
        }
    }

    private void createGoPkgLockWithInitAndEnsure(File location, File goDepExecutable) throws IOException {
        final File gopkgTomlFile = new File(location, "Gopkg.toml");
        final File vendorDirectory = new File(location, "vendor");
        final boolean vendorDirectoryExistedBefore = vendorDirectory.exists();
        final File vendorDirectoryBackup = new File(location, "vendor_old");
        if (vendorDirectoryExistedBefore) {
            logger.info(String.format("Backing up %s to %s", vendorDirectory.getAbsolutePath(), vendorDirectoryBackup.getAbsolutePath()));
            FileUtils.moveDirectory(vendorDirectory, vendorDirectoryBackup);
        }

        final String goDepInitString = String.format("%s 'init' on path %s", goDepExecutable, location.getAbsolutePath());
        try {
            logger.info("Running " + goDepInitString);
            executableRunner.execute(location, goDepExecutable, Arrays.asList("init"));
        } catch (final ExecutableRunnerException e) {
            logger.error(String.format("Failed to run %s: %s", goDepInitString, e.getMessage()));
        }

        final String goDepEnsureUpdateString = String.format("%s 'ensure -update' on path %s", goDepExecutable, location.getAbsolutePath());
        try {
            logger.info("Running " + goDepEnsureUpdateString);
            executableRunner.execute(location, goDepExecutable, Arrays.asList("ensure", "-update"));
        } catch (final ExecutableRunnerException e) {
            logger.error(String.format("Failed to run %s: %s", goDepEnsureUpdateString, e.getMessage()));
        }


        gopkgTomlFile.delete();
        FileUtils.deleteDirectory(vendorDirectory);
        if (vendorDirectoryExistedBefore) {
            logger.info(String.format("Restoring back up %s from %s", vendorDirectory.getAbsolutePath(), vendorDirectoryBackup.getAbsolutePath()));
            FileUtils.moveDirectory(vendorDirectoryBackup, vendorDirectory);
        }
    }


}
