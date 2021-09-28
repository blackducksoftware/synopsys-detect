/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.gradle.inspection;

import java.util.Optional;

import com.synopsys.integration.detectable.detectables.gradle.inspection.inspector.GradleInspectorScriptOptions;
import com.synopsys.integration.rest.proxy.ProxyInfo;

public class GradleInspectorOptions {
    private final String gradleBuildCommand;
    private final GradleInspectorScriptOptions gradleInspectorScriptOptions;
    private final ProxyInfo proxyInfo;
    private final boolean includeUnresolvedConfigurations;

    public GradleInspectorOptions(String gradleBuildCommand, GradleInspectorScriptOptions gradleInspectorScriptOptions, ProxyInfo proxyInfo, boolean includeUnresolvedConfigurations) {
        this.gradleBuildCommand = gradleBuildCommand;
        this.gradleInspectorScriptOptions = gradleInspectorScriptOptions;
        this.proxyInfo = proxyInfo;
        this.includeUnresolvedConfigurations = includeUnresolvedConfigurations;
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

    public boolean shouldIncludeUnresolvedConfigurations() {
        return includeUnresolvedConfigurations;
    }
}
