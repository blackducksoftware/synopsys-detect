/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.common.util.finder.FileFinder;
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
    private final Gson htmlEscapeDisabledGson;
    private final FileFinder fileFinder;

    public RunContext(DetectContext detectContext, ProductRunData productRunData, FileFinder fileFinder) {
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
        // Can't have more than one instance of Gson registered at the moment.  It causes problems resolving the beans for the application if there is more than one Gson.
        this.htmlEscapeDisabledGson = BlackDuckServicesFactory.createDefaultGsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        this.fileFinder = fileFinder;
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

    public Gson getHtmlEscapeDisabledGson() {
        return htmlEscapeDisabledGson;
    }

    public FileFinder getFileFinder() {
        return fileFinder;
    }
}
