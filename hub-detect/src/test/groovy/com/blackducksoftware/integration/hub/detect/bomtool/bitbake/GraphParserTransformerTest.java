package com.blackducksoftware.integration.hub.detect.bomtool.bitbake;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.paypal.digraph.parser.GraphParser;

public class GraphParserTransformerTest {
    @Test
    public void transform() throws IOException {
        final GraphParserTransformer graphParserTransformer = new GraphParserTransformer();
        final InputStream inputStream = new ClassPathResource("/bitbake/recipe-depends.dot").getInputStream();
        final GraphParser graphParser = new GraphParser(inputStream);
        final DependencyGraph dependencyGraph = graphParserTransformer.transform(graphParser);

        assert dependencyGraph.getRootDependencies().size() == 480;
    }
}