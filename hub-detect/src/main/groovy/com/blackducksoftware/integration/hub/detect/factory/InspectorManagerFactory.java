package com.blackducksoftware.integration.hub.detect.factory;

import javax.xml.parsers.DocumentBuilder;

import com.blackducksoftware.integration.hub.detect.bomtool.go.GoInspectorManager;
import com.blackducksoftware.integration.hub.detect.bomtool.gradle.GradleInspectorManager;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.NugetInspectorManager;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipInspectorManager;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigurationUtility;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

import freemarker.template.Configuration;

public class InspectorManagerFactory {

    private final ExecutableRunner executableRunner;
    private final DetectFileManager detectFileManager;
    private final DetectConfiguration detectConfiguration;
    private final ExecutableManager executableManager;
    private final DetectConfigurationUtility detectConfigurationUtility;
    private final DocumentBuilder documentBuilder;
    private final Configuration configuration;

    public InspectorManagerFactory(final ExecutableRunner executableRunner, final DetectFileManager detectFileManager,
        final DetectConfiguration detectConfiguration, final ExecutableManager executableManager, final DetectConfigurationUtility detectConfigurationUtility, final DocumentBuilder documentBuilder,
        final Configuration configuration) {
        this.executableRunner = executableRunner;
        this.detectFileManager = detectFileManager;
        this.detectConfiguration = detectConfiguration;
        this.executableManager = executableManager;
        this.detectConfigurationUtility = detectConfigurationUtility;
        this.documentBuilder = documentBuilder;
        this.configuration = configuration;
    }

    public GoInspectorManager goInspectorManager() {
        return new GoInspectorManager(detectFileManager, executableManager, executableRunner, detectConfiguration);
    }

    public GradleInspectorManager gradleInspectorManager() {
        //return new GradleInspectorManager(detectFileManager, configuration, documentBuilder, detectConfiguration, detectConfigurationUtility);
        return null;
    }

    public PipInspectorManager pipInspectorManager() {
        return new PipInspectorManager(detectFileManager);
    }

    public NugetInspectorManager nugetInspectorManager() {
        return new NugetInspectorManager(detectFileManager, executableManager, executableRunner, detectConfiguration);
    }
}
