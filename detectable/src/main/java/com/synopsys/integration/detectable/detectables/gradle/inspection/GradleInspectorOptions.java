package com.synopsys.integration.detectable.detectables.gradle.inspection;

import java.util.Optional;

import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.gradle.inspection.inspector.GradleInspectorScriptOptions;
import com.synopsys.integration.rest.proxy.ProxyInfo;

public class GradleInspectorOptions {
    private final String gradleBuildCommand;
    private final GradleInspectorScriptOptions gradleInspectorScriptOptions;
    private final ProxyInfo proxyInfo;
    private final EnumListFilter<GradleConfigurationType> configurationTypeFilter;

    public GradleInspectorOptions(
        String gradleBuildCommand,
        GradleInspectorScriptOptions gradleInspectorScriptOptions,
        ProxyInfo proxyInfo,
        EnumListFilter<GradleConfigurationType> configurationTypeFilter
    ) {
        this.gradleBuildCommand = gradleBuildCommand;
        this.gradleInspectorScriptOptions = gradleInspectorScriptOptions;
        this.proxyInfo = proxyInfo;
        this.configurationTypeFilter = configurationTypeFilter;
    }

    public Optional<String> getGradleBuildCommand() {
        return Optional.ofNullable(gradleBuildCommand);
    }

    public GradleInspectorScriptOptions getGradleInspectorScriptOptions() {
        return gradleInspectorScriptOptions;
    }

    public ProxyInfo getproxyInfo() {
        return proxyInfo;
    }

    public EnumListFilter<GradleConfigurationType> getConfigurationTypeFilter() {
        return configurationTypeFilter;
    }
}
