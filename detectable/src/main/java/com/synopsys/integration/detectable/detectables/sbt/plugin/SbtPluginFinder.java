package com.synopsys.integration.detectable.detectables.sbt.plugin;

import java.io.File;
import java.util.Collections;
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
    public static final String COURSIER_PLUGIN_NAME = "coursier.CoursierPlugin";
    private final DetectableExecutableRunner executableRunner;
    private final SbtPluginLineParser dependencyGraphPluginParser;
    private final SbtPluginLineParser coursierPluginParser;

    public SbtPluginFinder(final DetectableExecutableRunner executableRunner, final SbtDependencyGraphPluginLineParser dependencyGraphPluginParser,
        final SbtCoursierPluginLineParser coursierPluginParser) {
        this.executableRunner = executableRunner;
        this.dependencyGraphPluginParser = dependencyGraphPluginParser;
        this.coursierPluginParser = coursierPluginParser;
    }

    public SbtPlugin findPlugin(File directory, ExecutableTarget sbt) throws DetectableException {
        List<String> pluginOutput = listPlugins(directory, sbt);
        return determineInstalledPlugin(pluginOutput);
    }

    @Nullable
    private SbtPlugin determineInstalledPlugin(List<String> pluginOutput) {
        if (pluginOutput.stream().anyMatch(line -> line.contains(DEPENDENCY_GRAPH_PLUGIN_NAME))) {
            return new SbtPlugin("Dependency Graph", Collections.singletonList("dependencyTree"), dependencyGraphPluginParser);
        } else if (pluginOutput.stream().anyMatch(line -> line.contains(COURSIER_PLUGIN_NAME))) {
            return new SbtPlugin("Coursier", Collections.singletonList("coursierDependencyTree"), coursierPluginParser);
        } else {
            return null;
        }
    }

    private List<String> listPlugins(File directory, ExecutableTarget sbt) throws DetectableException {
        try {
            ExecutableOutput output = executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(directory, sbt, "plugins"));
            return output.getStandardOutputAsList();
        } catch (ExecutableFailedException e) {
            throw new DetectableException("Unable to list installed sbt plugins, detect requires a suitable sbt plugin is available to find dependency graphs.", e);
        }

    }
}
