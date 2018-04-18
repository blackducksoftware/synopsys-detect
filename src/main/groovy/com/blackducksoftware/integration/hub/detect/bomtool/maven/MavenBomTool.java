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
package com.blackducksoftware.integration.hub.detect.bomtool.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolExtractionResult;
import com.blackducksoftware.integration.hub.detect.hub.HubSignatureScanner;
import com.blackducksoftware.integration.hub.detect.hub.ScanPathSource;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;

import groovy.transform.TypeChecked;

@Component
@TypeChecked
class MavenBomTool extends BomTool<MavenApplicableResult> {
    public static final String POM_FILENAME = "pom.xml";
    public static final String POM_WRAPPER_FILENAME = "pom.groovy";
    private final Logger logger = LoggerFactory.getLogger(MavenBomTool.class);
    @Autowired
    private MavenCodeLocationPackager mavenCodeLocationPackager;

    @Autowired
    private HubSignatureScanner hubSignatureScanner;

    @Override
    public BomToolType getBomToolType() {
        return BomToolType.MAVEN;
    }

    @Override
    public MavenApplicableResult isBomToolApplicable(final File directory) {
        final File pomXmlFile = getDetectFileManager().findFile(directory, POM_FILENAME);
        final File pomWrapperFile = getDetectFileManager().findFile(directory, POM_WRAPPER_FILENAME);

        final boolean foundPomXml = null != pomXmlFile && pomXmlFile.exists();
        final boolean foundPomWrapper = null != pomWrapperFile && pomWrapperFile.exists();

        if (foundPomXml || foundPomWrapper) {
            final String mvnExecutablePath = findMavenExecutablePath(directory.toString());
            if (StringUtils.isNotBlank(mvnExecutablePath)) {
                return new MavenApplicableResult(directory, pomXmlFile, pomWrapperFile, mvnExecutablePath);
            } else {
                logger.warn("Could not find the Maven executable mvn, please ensure that Maven has been installed correctly.");
            }
        }

        return null;
    }

    @Override
    public BomToolExtractionResult extractDetectCodeLocations(final MavenApplicableResult applicable) {
        String mavenCommand = getDetectConfiguration().getMavenBuildCommand();
        if (StringUtils.isNotBlank(mavenCommand)) {
            mavenCommand = mavenCommand.replace("dependency:tree", "");
            if (StringUtils.isNotBlank(mavenCommand)) {
                mavenCommand = mavenCommand.trim();
            }
        }

        final List<String> arguments = new ArrayList<>();
        if (StringUtils.isNotBlank(mavenCommand)) {
            arguments.addAll(Arrays.asList(mavenCommand.split(" ")));
        }
        if (StringUtils.isNotBlank(getDetectConfiguration().getMavenScope())) {
            arguments.add(String.format("-Dscope=%s", getDetectConfiguration().getMavenScope()));
        }
        arguments.add("dependency:tree");

        List<DetectCodeLocation> codeLocations = null;
        try {
            final Executable mvnExecutable = new Executable(applicable.getDirectory(), applicable.getMavenExe(), arguments);
            final ExecutableOutput mvnOutput = getExecutableRunner().execute(mvnExecutable);

            final String excludedModules = getDetectConfiguration().getMavenExcludedModuleNames();
            final String includedModules = getDetectConfiguration().getMavenIncludedModuleNames();
            codeLocations = mavenCodeLocationPackager.extractCodeLocations(applicable.getDirectoryString(), mvnOutput.getStandardOutput(), excludedModules, includedModules);

            final List<File> additionalTargets = getDetectFileManager().findFilesToDepth(applicable.getDirectory(), "target", getDetectConfiguration().getSearchDepth());
            if (null != additionalTargets && !additionalTargets.isEmpty()) {
                for (final File additionalTarget : additionalTargets) {
                    hubSignatureScanner.registerPathToScan(ScanPathSource.MAVEN_SOURCE, additionalTarget);
                }
            }
        } catch (ExecutableRunnerException | IntegrationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return getBomToolExtractionResultsFactory().fromCodeLocations(codeLocations, getBomToolType(), applicable.getDirectory());
    }

    private String findMavenExecutablePath(final String directory) {
        if (StringUtils.isNotBlank(getDetectConfiguration().getMavenPath())) {
            return getDetectConfiguration().getMavenPath();
        }

        final String wrapperPath = getExecutableManager().getExecutablePath(ExecutableType.MVNW, false, directory);
        if (StringUtils.isNotBlank(wrapperPath)) {
            return wrapperPath;
        }

        return getExecutableManager().getExecutablePath(ExecutableType.MVN, true, directory);
    }
}
