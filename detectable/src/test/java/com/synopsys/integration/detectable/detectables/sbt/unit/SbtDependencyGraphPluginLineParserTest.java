package com.synopsys.integration.detectable.detectables.sbt.unit;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.detectable.detectables.sbt.plugin.SbtDependencyGraphPluginLineParser;

public class SbtDependencyGraphPluginLineParserTest {

    @Test
    public void urlIsNotDependency() {
        SbtDependencyGraphPluginLineParser parser = new SbtDependencyGraphPluginLineParser();
        Assertions.assertFalse(parser.tryParseLine("https://repo1.maven.org/maven2/org/scala-lang/scala-library/2.à (73.90 %, 42457à").isPresent());
    }

    @Test
    public void jarIsNotDependency() {
        SbtDependencyGraphPluginLineParser parser = new SbtDependencyGraphPluginLineParser();
        Assertions.assertFalse(parser.tryParseLine("Downloaded https://repo1.maven.org/maven2/org/scala-lang/scala-reflect/2.11.8/scala-reflect-2.11.8.jar").isPresent());
    }

    @Test
    public void whitespaceIsNotDependency() {
        SbtDependencyGraphPluginLineParser parser = new SbtDependencyGraphPluginLineParser();
        Assertions.assertFalse(parser.tryParseLine("").isPresent());
    }

    @Test
    public void updatingIsNotDependency() {
        SbtDependencyGraphPluginLineParser parser = new SbtDependencyGraphPluginLineParser();
        Assertions.assertFalse(parser.tryParseLine("[info] Updating {file:/C:/Users/jordanp/Downloads/sbt-example-master/}core...").isPresent());
    }

    @Test
    public void resolvingIsNotDependency() {
        SbtDependencyGraphPluginLineParser parser = new SbtDependencyGraphPluginLineParser();
        Assertions.assertFalse(parser.tryParseLine("[info] Resolving jline#jline;2.12.1 ...").isPresent());
    }
}
