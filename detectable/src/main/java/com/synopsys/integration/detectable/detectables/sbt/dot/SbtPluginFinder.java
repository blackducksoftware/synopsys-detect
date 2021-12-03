package com.synopsys.integration.detectable.detectables.sbt.dot;

import java.io.File;
import java.util.List;

import org.jetbrains.annotations.Nullable;
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
    private final SbtCommandArgumentGenerator sbtCommandArgumentGenerator;

    public SbtPluginFinder(DetectableExecutableRunner executableRunner, SbtCommandArgumentGenerator sbtCommandArgumentGenerator) {
        this.executableRunner = executableRunner;
        this.sbtCommandArgumentGenerator = sbtCommandArgumentGenerator;
    }

    public boolean isPluginInstalled(File directory, ExecutableTarget sbt, @Nullable String sbtCommandAdditionalArguments) throws DetectableException {
        List<String> pluginOutput = listPlugins(directory, sbt, sbtCommandAdditionalArguments);
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

    private List<String> listPlugins(File directory, ExecutableTarget sbt, @Nullable String sbtCommandAdditionalArguments) throws DetectableException {
        try {
            List<String> args = sbtCommandArgumentGenerator.generateSbtCmdArgs(sbtCommandAdditionalArguments, "plugins");
            ExecutableOutput output = executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(directory, sbt, args));
            return output.getStandardOutputAsList();
        } catch (ExecutableFailedException e) {
            throw new DetectableException("Unable to list installed sbt plugins, detect requires a suitable sbt plugin is available to find dependency graphs.", e);
        }

    }
}
