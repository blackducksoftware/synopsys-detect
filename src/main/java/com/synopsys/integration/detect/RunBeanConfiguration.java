/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detect;

import javax.xml.parsers.DocumentBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.BdioTransformer;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchRunner;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.detect.configuration.ConnectionManager;
import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.DetectableOptionFactory;
import com.synopsys.integration.detect.configuration.PropertyAuthority;
import com.synopsys.integration.detect.tool.detector.DetectFileFinder;
import com.synopsys.integration.detect.tool.detector.impl.DetectExecutableResolver;
import com.synopsys.integration.detect.tool.detector.inspectors.ArtifactoryDockerInspectorResolver;
import com.synopsys.integration.detect.tool.detector.inspectors.DockerInspectorInstaller;
import com.synopsys.integration.detect.tool.signaturescanner.BlackDuckSignatureScanner;
import com.synopsys.integration.detect.tool.signaturescanner.BlackDuckSignatureScannerOptions;
import com.synopsys.integration.detect.workflow.ArtifactResolver;
import com.synopsys.integration.detect.workflow.DetectRun;
import com.synopsys.integration.detect.workflow.airgap.AirGapInspectorPaths;
import com.synopsys.integration.detect.workflow.airgap.AirGapOptions;
import com.synopsys.integration.detect.workflow.airgap.AirGapPathFinder;
import com.synopsys.integration.detect.workflow.codelocation.BdioCodeLocationCreator;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameGenerator;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleExecutableFinder;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleExecutableResolver;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleLocalExecutableFinder;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleSystemExecutableFinder;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.file.impl.SimpleFileFinder;
import com.synopsys.integration.detectable.detectables.docker.DockerInspectorResolver;

import freemarker.template.Configuration;

@org.springframework.context.annotation.Configuration
public class RunBeanConfiguration {
    @Autowired
    public DetectRun detectRun;
    @Autowired
    public DetectInfo detectInfo;
    @Autowired
    public DetectConfiguration detectConfiguration;
    @Autowired
    public DirectoryManager directoryManager;
    @Autowired
    public EventSystem eventSystem;
    @Autowired
    public Gson gson;
    @Autowired
    public Configuration configuration;
    @Autowired
    public DocumentBuilder documentBuilder;
    @Autowired
    public DetectableOptionFactory detectableOptionFactory;

    @Bean
    public ExternalIdFactory externalIdFactory() {
        return new ExternalIdFactory();
    }

    // This is not a bean!
    public FileFinder simpleFileFinder() {
        return new SimpleFileFinder();
    }

    @Bean
    public FileFinder fileFinder() {
        return new DetectFileFinder(detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_DETECTOR_SEARCH_EXCLUSION_FILES, PropertyAuthority.None));
    }

    @Bean
    public ConnectionManager connectionManager() {
        return new ConnectionManager(detectConfiguration);
    }

    @Bean
    public ArtifactResolver artifactResolver() {
        return new ArtifactResolver(connectionManager(), gson);
    }

    @Bean
    public AirGapPathFinder airGapPathFinder() {
        return new AirGapPathFinder();
    }

    @Bean
    public DetectConfigurationFactory detectConfigurationFactory() {
        return new DetectConfigurationFactory(detectConfiguration);
    }

    @Bean
    public CodeLocationNameGenerator codeLocationNameService() {
        String codeLocationNameOverride = detectConfiguration.getProperty(DetectProperty.DETECT_CODE_LOCATION_NAME, PropertyAuthority.None);
        return new CodeLocationNameGenerator(codeLocationNameOverride);
    }

    @Bean
    public CodeLocationNameManager codeLocationNameManager() {
        return new CodeLocationNameManager(codeLocationNameService());
    }

    @Bean
    public BdioCodeLocationCreator detectCodeLocationManager() {
        return new BdioCodeLocationCreator(codeLocationNameManager(), detectConfiguration, directoryManager, eventSystem);
    }

    @Bean
    public AirGapInspectorPaths airGapManager() {
        final AirGapOptions airGapOptions = detectConfigurationFactory().createAirGapOptions();
        return new AirGapInspectorPaths(airGapPathFinder(), airGapOptions);
    }

    @Bean
    public BdioTransformer bdioTransformer() {
        return new BdioTransformer();
    }

    @Bean
    public ExecutableRunner executableRunner() {
        return new SimpleExecutableRunner();
    }

    @Bean
    public SimpleExecutableFinder simpleExecutableFinder() {
        return SimpleExecutableFinder.forCurrentOperatingSystem(simpleFileFinder());
    }

    @Bean
    public SimpleLocalExecutableFinder simpleLocalExecutableFinder() {
        return new SimpleLocalExecutableFinder(simpleExecutableFinder());
    }

    @Bean
    public SimpleSystemExecutableFinder simpleSystemExecutableFinder() {
        return new SimpleSystemExecutableFinder(simpleExecutableFinder());
    }

    @Bean
    public SimpleExecutableResolver simpleExecutableResolver() {
        return new SimpleExecutableResolver(detectableOptionFactory.createCachedExecutableResolverOptions(), simpleLocalExecutableFinder(), simpleSystemExecutableFinder());
    }

    @Bean
    public DetectExecutableResolver detectExecutableResolver() {
        return new DetectExecutableResolver(simpleExecutableResolver(), detectConfiguration);
    }

    @Bean
    public DockerInspectorResolver dockerInspectorResolver() {
        DockerInspectorInstaller dockerInspectorInstaller = new DockerInspectorInstaller(artifactResolver());
        return new ArtifactoryDockerInspectorResolver(directoryManager, airGapManager(), simpleFileFinder(), dockerInspectorInstaller, detectableOptionFactory.createDockerDetectableOptions());
    }

    @Lazy
    @Bean()
    public BlackDuckSignatureScanner blackDuckSignatureScanner(final BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions, final ScanBatchRunner scanBatchRunner, final BlackDuckServerConfig blackDuckServerConfig) {
        return new BlackDuckSignatureScanner(directoryManager, simpleFileFinder(), codeLocationNameManager(), blackDuckSignatureScannerOptions, eventSystem, scanBatchRunner, blackDuckServerConfig);
    }
}
