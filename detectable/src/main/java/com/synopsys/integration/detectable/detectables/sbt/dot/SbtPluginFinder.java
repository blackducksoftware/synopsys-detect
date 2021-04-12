/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.sbt.dot;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.executable.ExecutableOutput;

public class SbtPluginFinder {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String DEPENDENCY_GRAPH_PLUGIN_NAME = "net.virtualvoid.sbt.graph.DependencyGraphPlugin";
    public static final String DEPENDENCY_GRAPH_SBT_INTERNAL_PLUGIN_NAME = "sbt.plugins.DependencyTreePlugin";
    private final DetectableExecutableRunner executableRunner;

    public SbtPluginFinder(DetectableExecutableRunner executableRunner) {
        this.executableRunner = executableRunner;
    }

    public boolean isPluginInstalled(File directory, ExecutableTarget sbt) throws DetectableException {
        List<String> pluginOutput = listPlugins(directory, sbt);
        return determineInstalledPlugin(pluginOutput);
    }

    public boolean determineInstalledPlugin(List<String> pluginOutput) {
        if (pluginOutput.stream().anyMatch(line ->
                                               line.contains(DEPENDENCY_GRAPH_PLUGIN_NAME) || line.contains(DEPENDENCY_GRAPH_SBT_INTERNAL_PLUGIN_NAME))) {
            return true;
        } else {
            return false;
        }
    }

    private List<String> listPlugins(File directory, ExecutableTarget sbt) throws DetectableException {
        try {
            ExecutableOutput output = executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(directory, sbt, "-Djline.terminal=jline.UnsupportedTerminal", "plugins"));
            return output.getStandardOutputAsList();
        } catch (ExecutableFailedException e) {
            throw new DetectableException("Unable to list installed sbt plugins, detect requires a suitable sbt plugin is available to find dependency graphs.", e);
        }

    }
}
