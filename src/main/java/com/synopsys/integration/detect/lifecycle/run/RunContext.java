/**
 * synopsys-detect
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
package com.synopsys.integration.detect.lifecycle.run;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.lifecycle.DetectContext;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.tool.detector.CodeLocationConverter;
import com.synopsys.integration.detect.tool.detector.DetectDetectableFactory;
import com.synopsys.integration.detect.tool.detector.extraction.ExtractionEnvironmentProvider;
import com.synopsys.integration.detect.workflow.codelocation.BdioCodeLocationCreator;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameGenerator;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorResolver;

public class RunContext {
    private final DetectContext detectContext;
    private final ProductRunData productRunData;
    private final PropertyConfiguration detectConfiguration;
    private final DetectConfigurationFactory detectConfigurationFactory;
    private final DirectoryManager directoryManager;
    private final EventSystem eventSystem;
    private final CodeLocationNameGenerator codeLocationNameService;
    private final CodeLocationNameManager codeLocationNameManager;
    private final BdioCodeLocationCreator bdioCodeLocationCreator;
    private final DetectInfo detectInfo;
    private final NugetInspectorResolver nugetInspectorResolver;
    private final DetectDetectableFactory detectDetectableFactory;
    private final ExtractionEnvironmentProvider extractionEnvironmentProvider;
    private final CodeLocationConverter codeLocationConverter;
    private final Gson gson;

    public RunContext(DetectContext detectContext, ProductRunData productRunData) {
        this.detectContext = detectContext;
        this.productRunData = productRunData;
        detectConfiguration = detectContext.getBean(PropertyConfiguration.class);
        detectConfigurationFactory = detectContext.getBean(DetectConfigurationFactory.class);
        directoryManager = detectContext.getBean(DirectoryManager.class);
        eventSystem = detectContext.getBean(EventSystem.class);
        codeLocationNameService = detectContext.getBean(CodeLocationNameGenerator.class);
        codeLocationNameManager = detectContext.getBean(CodeLocationNameManager.class, codeLocationNameService);
        bdioCodeLocationCreator = detectContext.getBean(BdioCodeLocationCreator.class);
        detectInfo = detectContext.getBean(DetectInfo.class);
        nugetInspectorResolver = detectContext.getBean(NugetInspectorResolver.class);
        detectDetectableFactory = detectContext.getBean(DetectDetectableFactory.class, nugetInspectorResolver);
        extractionEnvironmentProvider = new ExtractionEnvironmentProvider(directoryManager);
        codeLocationConverter = new CodeLocationConverter(new ExternalIdFactory());
        gson = detectContext.getBean(Gson.class);
    }

    public DetectContext getDetectContext() {
        return detectContext;
    }

    public ProductRunData getProductRunData() {
        return productRunData;
    }

    public PropertyConfiguration getDetectConfiguration() {
        return detectConfiguration;
    }

    public DetectConfigurationFactory getDetectConfigurationFactory() {
        return detectConfigurationFactory;
    }

    public DirectoryManager getDirectoryManager() {
        return directoryManager;
    }

    public EventSystem getEventSystem() {
        return eventSystem;
    }

    public CodeLocationNameGenerator getCodeLocationNameService() {
        return codeLocationNameService;
    }

    public CodeLocationNameManager getCodeLocationNameManager() {
        return codeLocationNameManager;
    }

    public BdioCodeLocationCreator getBdioCodeLocationCreator() {
        return bdioCodeLocationCreator;
    }

    public DetectInfo getDetectInfo() {
        return detectInfo;
    }

    public NugetInspectorResolver getNugetInspectorResolver() {
        return nugetInspectorResolver;
    }

    public DetectDetectableFactory getDetectDetectableFactory() {
        return detectDetectableFactory;
    }

    public ExtractionEnvironmentProvider getExtractionEnvironmentProvider() {
        return extractionEnvironmentProvider;
    }

    public CodeLocationConverter getCodeLocationConverter() {
        return codeLocationConverter;
    }

    public RunOptions createRunOptions() {
        return detectConfigurationFactory.createRunOptions();
    }

    public Gson getGson() {
        return gson;
    }
}
