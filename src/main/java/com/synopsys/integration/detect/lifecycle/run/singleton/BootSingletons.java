package com.synopsys.integration.detect.lifecycle.run.singleton;

import com.google.gson.Gson;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectableOptionFactory;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.tool.cache.InstalledToolLocator;
import com.synopsys.integration.detect.tool.cache.InstalledToolManager;
import com.synopsys.integration.detect.workflow.DetectRunId;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.profiling.DetectorProfiler;

import freemarker.template.Configuration;

//Everything a DetectRun needs supplied from Boot. Ideally the minimum subset of things needed to be passed from Boot to Run.
public class BootSingletons {
    private final ProductRunData productRunData;

    private final DetectRunId detectRunId;
    private final Gson gson;
    private final DetectInfo detectInfo;

    private final FileFinder fileFinder;
    private final EventSystem eventSystem;
    private final DetectorProfiler detectorProfiler;

    private final PropertyConfiguration detectConfiguration;
    private final DetectConfigurationFactory detectConfigurationFactory;
    private final DetectableOptionFactory detectableOptionFactory; //This is the only one that I do not believe should be here. Does not feel like it should be owned by boot. Currently requires 'diagnostics' sadly. - jp

    private final DirectoryManager directoryManager;
    private final Configuration configuration;

    private final InstalledToolManager installedToolManager;
    private final InstalledToolLocator installedToolLocator;

    public BootSingletons(
        ProductRunData productRunData,
        DetectRunId detectRunId,
        Gson gson,
        DetectInfo detectInfo,
        FileFinder fileFinder,
        EventSystem eventSystem,
        DetectorProfiler detectorProfiler,
        PropertyConfiguration detectConfiguration,
        DetectableOptionFactory detectableOptionFactory,
        DetectConfigurationFactory detectConfigurationFactory,
        DirectoryManager directoryManager,
        Configuration configuration,
        InstalledToolManager installedToolManager,
        InstalledToolLocator installedToolLocator
    ) {
        this.productRunData = productRunData;
        this.detectRunId = detectRunId;
        this.gson = gson;
        this.detectInfo = detectInfo;
        this.fileFinder = fileFinder;
        this.eventSystem = eventSystem;
        this.detectorProfiler = detectorProfiler;
        this.detectConfiguration = detectConfiguration;
        this.detectConfigurationFactory = detectConfigurationFactory;
        this.detectableOptionFactory = detectableOptionFactory;
        this.directoryManager = directoryManager;
        this.configuration = configuration;
        this.installedToolManager = installedToolManager;
        this.installedToolLocator = installedToolLocator;
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

    public DetectInfo getDetectInfo() {
        return detectInfo;
    }

    public Gson getGson() {
        return gson;
    }

    public FileFinder getFileFinder() {
        return fileFinder;
    }

    public DetectRunId getDetectRunId() {
        return detectRunId;
    }

    public DetectorProfiler getDetectorProfiler() {
        return detectorProfiler;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public DetectableOptionFactory getDetectableOptionFactory() {
        return detectableOptionFactory;
    }

    public InstalledToolManager getInstalledToolManager() {
        return installedToolManager;
    }

    public InstalledToolLocator getInstalledToolLocator() {
        return installedToolLocator;
    }
}
