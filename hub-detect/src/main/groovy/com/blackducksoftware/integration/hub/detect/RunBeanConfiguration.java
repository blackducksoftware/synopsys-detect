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
package com.blackducksoftware.integration.hub.detect;

import javax.xml.parsers.DocumentBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import com.blackducksoftware.integration.hub.detect.configuration.ConnectionManager;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.detector.DetectorEnvironment;
import com.blackducksoftware.integration.hub.detect.tool.docker.DockerDetector;
import com.blackducksoftware.integration.hub.detect.tool.docker.DockerExtractor;
import com.blackducksoftware.integration.hub.detect.tool.docker.DockerInspectorManager;
import com.blackducksoftware.integration.hub.detect.tool.docker.DockerProperties;
import com.blackducksoftware.integration.hub.detect.tool.signaturescanner.BlackDuckSignatureScannerOptions;
import com.blackducksoftware.integration.hub.detect.tool.signaturescanner.OfflineBlackDuckSignatureScanner;
import com.blackducksoftware.integration.hub.detect.tool.signaturescanner.OnlineBlackDuckSignatureScanner;
import com.blackducksoftware.integration.hub.detect.util.executable.CacheableExecutableFinder;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableFinder;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.workflow.ArtifactResolver;
import com.blackducksoftware.integration.hub.detect.workflow.ConnectivityManager;
import com.blackducksoftware.integration.hub.detect.workflow.DetectConfigurationFactory;
import com.blackducksoftware.integration.hub.detect.workflow.DetectRun;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.BdioCodeLocationCreator;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.CodeLocationNameGenerator;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.CodeLocationNameManager;
import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.DiagnosticManager;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.file.AirGapManager;
import com.blackducksoftware.integration.hub.detect.workflow.file.AirGapOptions;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.synopsys.integration.bdio.BdioTransformer;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationService;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchRunner;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;

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
    public DiagnosticManager diagnosticManager;
    @Autowired
    public ConnectivityManager connectivityManager;
    @Autowired
    public EventSystem eventSystem;
    @Autowired
    public Gson gson;
    @Autowired
    public Configuration configuration;
    @Autowired
    public DocumentBuilder documentBuilder;

    @Bean
    public ExternalIdFactory externalIdFactory() {
        return new ExternalIdFactory();
    }

    @Bean
    public DetectFileFinder detectFileFinder() {
        return new DetectFileFinder();
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
    public DetectConfigurationFactory detectConfigurationFactory() {
        return new DetectConfigurationFactory(detectConfiguration);
    }

    @Bean
    public CodeLocationNameGenerator codeLocationNameService() {
        return new CodeLocationNameGenerator(detectFileFinder());
    }

    @Bean
    public CodeLocationNameManager codeLocationNameManager() {
        return new CodeLocationNameManager(detectConfiguration, codeLocationNameService());
    }

    @Bean
    public BdioCodeLocationCreator detectCodeLocationManager() {
        return new BdioCodeLocationCreator(codeLocationNameManager(), detectConfiguration, directoryManager, eventSystem);
    }

    @Bean
    public ExecutableRunner executableRunner() {
        return new ExecutableRunner();
    }

    @Bean
    public ExecutableFinder executableManager() {
        return new ExecutableFinder(detectFileFinder(), detectInfo);
    }

    @Bean
    public AirGapManager airGapManager() {
        AirGapOptions airGapOptions = detectConfigurationFactory().createAirGapOptions();
        return new AirGapManager(airGapOptions);
    }

    @Bean
    public BdioTransformer bdioTransformer() {
        return new BdioTransformer();
    }

    @Bean
    public CacheableExecutableFinder standardExecutableFinder() {
        return new CacheableExecutableFinder(directoryManager, executableManager(), detectConfiguration);
    }

    @Bean
    public DockerInspectorManager dockerInspectorManager() {
        return new DockerInspectorManager(directoryManager, airGapManager(), detectFileFinder(), detectConfiguration, artifactResolver());
    }

    @Lazy
    @Bean
    public DockerDetector dockerBomTool(DetectorEnvironment detectorEnvironment) {
        DockerProperties dockerProperties = new DockerProperties(detectConfiguration);

        final String tar = detectConfiguration.getProperty(DetectProperty.DETECT_DOCKER_TAR, PropertyAuthority.None);
        final String image = detectConfiguration.getProperty(DetectProperty.DETECT_DOCKER_IMAGE, PropertyAuthority.None);
        final boolean dockerRequired = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_DOCKER_PATH_REQUIRED, PropertyAuthority.None);

        return new DockerDetector(detectInfo, detectorEnvironment, directoryManager, dockerInspectorManager(), standardExecutableFinder(), dockerRequired, image, tar, dockerExtractor(dockerProperties));
    }

    @Lazy
    @Bean
    public DockerExtractor dockerExtractor(DockerProperties dockerProperties) {
        return new DockerExtractor(detectFileFinder(), dockerProperties, executableRunner(), bdioTransformer(), externalIdFactory(), gson);
    }

    @Lazy
    @Bean
    public OnlineBlackDuckSignatureScanner onlineBlackDuckSignatureScanner(BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions, ScanBatchRunner scanBatchRunner, CodeLocationCreationService codeLocationCreationService,
        BlackDuckServerConfig hubServerConfig) {
        return new OnlineBlackDuckSignatureScanner(directoryManager, detectFileFinder(), codeLocationNameManager(), blackDuckSignatureScannerOptions, eventSystem, scanBatchRunner, codeLocationCreationService, hubServerConfig);
    }

    @Lazy
    @Bean
    public OfflineBlackDuckSignatureScanner offlineBlackDuckSignatureScanner(BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions, ScanBatchRunner scanBatchRunner) {
        return new OfflineBlackDuckSignatureScanner(directoryManager, detectFileFinder(), codeLocationNameManager(), blackDuckSignatureScannerOptions, eventSystem, scanBatchRunner);
    }

}
