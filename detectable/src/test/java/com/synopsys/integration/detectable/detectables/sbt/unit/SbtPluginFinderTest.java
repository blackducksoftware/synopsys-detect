package com.synopsys.integration.detectable.detectables.sbt.unit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.sbt.dot.SbtCommandArgumentGenerator;
import com.synopsys.integration.detectable.detectables.sbt.dot.SbtPluginFinder;

public class SbtPluginFinderTest {

    @Test
    public void pluginNotFoundForEmptyOutput() {
        SbtPluginFinder parser = new SbtPluginFinder(null, new SbtCommandArgumentGenerator());
        List<String> input = Collections.singletonList("");
        Assertions.assertFalse(parser.determineInstalledPlugin(input), "Plugin should have NOT have been found!");
    }

    @Test
    public void pluginNotFoundEvenWithWordGraph() {
        SbtPluginFinder parser = new SbtPluginFinder(null, new SbtCommandArgumentGenerator());
        List<String> input = Collections.singletonList("someother.DependencyGraphPlugin");
        Assertions.assertFalse(parser.determineInstalledPlugin(input), "Plugin should have NOT have been found!");
    }

    @Test
    public void pluginFoundNormally() {
        SbtPluginFinder parser = new SbtPluginFinder(null, new SbtCommandArgumentGenerator());
        List<String> input = Arrays.asList(
            "[info] welcome to sbt 1.4.7 (Oracle Corporation Java 1.8.0_161)", //standard sbt preamble
            "[info] loading settings for project scalafmt-build from plugins.sbt ...",
            "[info] loading project definition from C:\\Users\\jordanp\\Downloads\\scalafmt-master\\scalafmt\\project",
            "[info] loading settings for project scalafmt from build.sbt ...",
            "[info] set current project to scalafmtRoot (in build file:/C:/Users/jordanp/Downloads/scalafmt-master/scalafmt/)",
            "In build /C:/Users/jordanp/Downloads/scalafmt-master/scalafmt/:",
            "  Enabled plugins in benchmarks:",
            "    net.virtualvoid.sbt.graph.DependencyGraphPlugin"
        ); //plugin we should find.
        Assertions.assertTrue(parser.determineInstalledPlugin(input), "Plugin should have been found!");
    }

    @Test
    public void pluginInternalFoundNormally() {
        SbtPluginFinder parser = new SbtPluginFinder(null, new SbtCommandArgumentGenerator());
        List<String> input = Arrays.asList(
            "[info] welcome to sbt 1.4.7 (Oracle Corporation Java 1.8.0_161)", //standard sbt preamble
            "[info] loading settings for project scalafmt-build from plugins.sbt ...",
            "[info] loading project definition from C:\\Users\\jordanp\\Downloads\\scalafmt-master\\scalafmt\\project",
            "[info] loading settings for project scalafmt from build.sbt ...",
            "[info] set current project to scalafmtRoot (in build file:/C:/Users/jordanp/Downloads/scalafmt-master/scalafmt/)",
            "In build /C:/Users/jordanp/Downloads/scalafmt-master/scalafmt/:",
            "  Enabled plugins in benchmarks:",
            "    sbt.plugins.DependencyTreePlugin"
        ); //plugin we should find.
        Assertions.assertTrue(parser.determineInstalledPlugin(input), "Plugin should have been found!");
    }

    @Test
    public void pluginNotFoundNormally() {
        SbtPluginFinder parser = new SbtPluginFinder(null, new SbtCommandArgumentGenerator());
        List<String> input = Arrays.asList(
            "[info] welcome to sbt 1.4.7 (Oracle Corporation Java 1.8.0_161)", //standard sbt preamble
            "[info] loading settings for project scalafmt-build from plugins.sbt ...",
            "[info] loading project definition from C:\\Users\\jordanp\\Downloads\\scalafmt-master\\scalafmt\\project",
            "[info] loading settings for project scalafmt from build.sbt ...",
            "[info] set current project to scalafmtRoot (in build file:/C:/Users/jordanp/Downloads/scalafmt-master/scalafmt/)",
            "In build /C:/Users/jordanp/Downloads/scalafmt-master/scalafmt/:",
            "  Enabled plugins in compile:",
            "    com.typesafe.sbt.packager.archetypes.JavaServerAppPackaging",//should ignore
            "    com.typesafe.sbt.packager.archetypes.jar.ClasspathJarPlugin",//should ignore
            "    com.typesafe.sbt.packager.archetypes.jar.LauncherJarPlugin"
        ); //should ignore
        Assertions.assertFalse(parser.determineInstalledPlugin(input), "Plugin should NOT have been found!");
    }
}
