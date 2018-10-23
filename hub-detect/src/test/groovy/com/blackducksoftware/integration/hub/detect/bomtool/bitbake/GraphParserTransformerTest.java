package com.blackducksoftware.integration.hub.detect.bomtool.bitbake;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.paypal.digraph.parser.GraphParser;
import com.synopsys.integration.hub.bdio.graph.DependencyGraph;

public class GraphParserTransformerTest {
    @Test
    public void transform() throws IOException {
        final GraphParserTransformer graphParserTransformer = new GraphParserTransformer();
        final InputStream inputStream = new ClassPathResource("/bitbake/recipe-depends.dot").getInputStream();
        final GraphParser graphParser = new GraphParser(inputStream);
        final DependencyGraph dependencyGraph = graphParserTransformer.transform(graphParser, "i586-poky-linux");

        assert dependencyGraph.getRootDependencies().size() == 480;
    }
}