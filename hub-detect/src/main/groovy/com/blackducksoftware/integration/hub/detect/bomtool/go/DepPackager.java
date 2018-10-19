/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.bomtool.go;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.synopsys.integration.hub.bdio.graph.DependencyGraph;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class DepPackager {
    private final Logger logger = LoggerFactory.getLogger(DepPackager.class);

    private final ExecutableRunner executableRunner;
    private final ExternalIdFactory externalIdFactory;
    private final DetectConfiguration detectConfiguration;

    public DepPackager(final ExecutableRunner executableRunner, final ExternalIdFactory externalIdFactory, final DetectConfiguration detectConfiguration) {
        this.executableRunner = executableRunner;
        this.externalIdFactory = externalIdFactory;
        this.detectConfiguration = detectConfiguration;
    }

    public DependencyGraph makeDependencyGraph(final String sourcePath, final String goDepExecutable) throws IOException {
        final GopkgLockParser gopkgLockParser = new GopkgLockParser(externalIdFactory);
        final String goDepContents = getGopkgLockContents(new File(sourcePath), goDepExecutable);
        if (StringUtils.isNotBlank(goDepContents)) {
            return gopkgLockParser.parseDepLock(goDepContents);
        }
        return null;
    }

    private String getGopkgLockContents(final File file, final String goDepExecutable) throws IOException {
        String gopkgLockContents = null;

        final File gopkgLockFile = new File(file, "Gopkg.lock");
        if (gopkgLockFile.exists()) {
            try (FileInputStream fis = new FileInputStream(gopkgLockFile)) {
                gopkgLockContents = IOUtils.toString(fis, Charset.defaultCharset());
            } catch (final Exception e) {
                gopkgLockContents = null;
            }
            return gopkgLockContents;
        }

        // by default, we won't run 'init' and 'ensure' anymore so just return an empty string
        if (!detectConfiguration.getBooleanProperty(DetectProperty.DETECT_GO_RUN_DEP_INIT, PropertyAuthority.None)) {
            logger.info("Skipping Dep commands 'init' and 'ensure'");
            return "";
        }

        final File gopkgTomlFile = new File(file, "Gopkg.toml");
        final File vendorDirectory = new File(file, "vendor");
        final boolean vendorDirectoryExistedBefore = vendorDirectory.exists();
        final File vendorDirectoryBackup = new File(file, "vendor_old");
        if (vendorDirectoryExistedBefore) {
            logger.info(String.format("Backing up %s to %s", vendorDirectory.getAbsolutePath(), vendorDirectoryBackup.getAbsolutePath()));
            FileUtils.moveDirectory(vendorDirectory, vendorDirectoryBackup);
        }

        final String goDepInitString = String.format("%s 'init' on path %s", goDepExecutable, file.getAbsolutePath());
        try {
            logger.info("Running " + goDepInitString);
            final Executable executable = new Executable(file, goDepExecutable, Arrays.asList("init"));
            executableRunner.execute(executable);
        } catch (final ExecutableRunnerException e) {
            logger.error(String.format("Failed to run %s: %s", goDepInitString, e.getMessage()));
        }

        final String goDepEnsureUpdateString = String.format("%s 'ensure -update' on path %s", goDepExecutable, file.getAbsolutePath());
        try {
            logger.info("Running " + goDepEnsureUpdateString);
            final Executable executable = new Executable(file, goDepExecutable, Arrays.asList("ensure", "-update"));
            executableRunner.execute(executable);
        } catch (final ExecutableRunnerException e) {
            logger.error(String.format("Failed to run %s: %s", goDepEnsureUpdateString, e.getMessage()));
        }

        if (gopkgLockFile.exists()) {
            try (FileInputStream fis = new FileInputStream(gopkgLockFile)) {
                gopkgLockContents = IOUtils.toString(fis, Charset.defaultCharset());
            } catch (final Exception e) {
                gopkgLockContents = null;
            }
            gopkgLockFile.delete();
            gopkgTomlFile.delete();
            FileUtils.deleteDirectory(vendorDirectory);
            if (vendorDirectoryExistedBefore) {
                logger.info(String.format("Restoring back up %s from %s", vendorDirectory.getAbsolutePath(), vendorDirectoryBackup.getAbsolutePath()));
                FileUtils.moveDirectory(vendorDirectoryBackup, vendorDirectory);
            }
        }

        return gopkgLockContents;
    }
}
