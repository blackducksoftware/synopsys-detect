package com.synopsys.integration.detectable.detectables.sbt.unit;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.sbt.dot.SbtDotOutputParser;

public class SbtDotOutputParserTest {
    @Test
    public void canParseSingleDotFile() {
        SbtDotOutputParser parser = new SbtDotOutputParser();
        List<File> results = parser.parseGeneratedGraphFiles(Collections.singletonList(
            "[info] Wrote dependency graph to 'C:\\Users\\jordanp\\Downloads\\scalafmt-master\\scalafmt\\scalafmt-interfaces\\target\\dependencies-compile.dot'"));
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("C:\\Users\\jordanp\\Downloads\\scalafmt-master\\scalafmt\\scalafmt-interfaces\\target\\dependencies-compile.dot", results.get(0).toString());
    }

    @Test
    public void ignoresNonGraphLines() {
        List<String> input = Arrays.asList(
            "[info] welcome to sbt 1.4.7 (Oracle Corporation Java 1.8.0_161)",
            "[info] loading settings for project scalafmt-build from plugins.sbt ...",
            "[info] loading project definition from C:\\Users\\jordanp\\Downloads\\scalafmt-master\\scalafmt\\project",
            "[info] loading settings for project scalafmt from build.sbt ...",
            "[info] set current project to scalafmtRoot (in build file:/C:/Users/jordanp/Downloads/scalafmt-master/scalafmt/)",
            "[info] Wrote dependency graph to 'C:\\Users\\jordanp\\Downloads\\scalafmt-master\\scalafmt\\scalafmt-interfaces\\target\\dependencies-compile.dot'"
        );
        SbtDotOutputParser parser = new SbtDotOutputParser();
        List<File> results = parser.parseGeneratedGraphFiles(input);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("C:\\Users\\jordanp\\Downloads\\scalafmt-master\\scalafmt\\scalafmt-interfaces\\target\\dependencies-compile.dot", results.get(0).toString());
    }

    @Test
    public void parsesMultipleGraphs() {
        List<String> input = Arrays.asList(
            "[info] Wrote dependency graph to 'C:\\Users\\jordanp\\Downloads\\scalafmt-master\\scalafmt\\scalafmt-interfaces\\target\\dependencies-compile.dot'",
            "[info] Wrote dependency graph to 'C:\\Users\\jordanp\\Downloads\\scalafmt-master\\scalafmt\\target\\dependencies-compile.dot'",
            "[info] Wrote dependency graph to 'C:\\Users\\jordanp\\Downloads\\scalafmt-master\\scalafmt\\scalafmt-dynamic\\target\\dependencies-compile.dot'"
        );
        SbtDotOutputParser parser = new SbtDotOutputParser();
        List<File> results = parser.parseGeneratedGraphFiles(input);
        Assertions.assertEquals(3, results.size());
        Assertions.assertEquals("C:\\Users\\jordanp\\Downloads\\scalafmt-master\\scalafmt\\scalafmt-interfaces\\target\\dependencies-compile.dot", results.get(0).toString());
        Assertions.assertEquals("C:\\Users\\jordanp\\Downloads\\scalafmt-master\\scalafmt\\target\\dependencies-compile.dot", results.get(1).toString());
        Assertions.assertEquals("C:\\Users\\jordanp\\Downloads\\scalafmt-master\\scalafmt\\scalafmt-dynamic\\target\\dependencies-compile.dot", results.get(2).toString());
    }

    @Test
    public void parsesMultipleGraphsNIXPaths() {
        List<String> input = Arrays.asList(
            "[warn] There may be incompatibilities among your library dependencies; run 'evicted' to see detailed eviction warnings.",
            "[info] Wrote dependency graph to '/Users/jordanp/scalafmt-master/scalafmt/scalafmt-interfaces/target/dependencies-compile.dot'",
            "[warn] There may be incompatibilities among your library dependencies; run 'evicted' to see detailed eviction warnings.",
            "[warn] There may be incompatibilities among your library dependencies; run 'evicted' to see detailed eviction warnings.",
            "[info] Wrote dependency graph to '/Users/jordanp/scalafmt-master/scalafmt/target/dependencies-compile.dot'",
            "[info] Wrote dependency graph to '/Users/jordanp/scalafmt-master/scalafmt/scalafmt-dynamic/target/dependencies-compile.dot'"
        );
        SbtDotOutputParser parser = new SbtDotOutputParser();
        List<File> results = parser.parseGeneratedGraphFiles(input);
        Assertions.assertEquals(3, results.size());
        Assertions.assertEquals(new File("/Users/jordanp/scalafmt-master/scalafmt/scalafmt-interfaces/target/dependencies-compile.dot"), results.get(0));
        Assertions.assertEquals(new File("/Users/jordanp/scalafmt-master/scalafmt/target/dependencies-compile.dot"), results.get(1));
        Assertions.assertEquals(new File("/Users/jordanp/scalafmt-master/scalafmt/scalafmt-dynamic/target/dependencies-compile.dot"), results.get(2));
    }

}
