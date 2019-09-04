package com.synopsys.integration.detectable.detectables.go.functional;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.gradle.gogradle.GoGradleLockParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.graph.GraphAssert;

class GoGradleLockParserTest {
    @Test
    void parseTest() throws IOException {
        final File goGradleLockFile = FunctionalTestFiles.asFile("/go/gogradle/gogradle.lock");
        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final GoGradleLockParser goGradleLockParser = new GoGradleLockParser(externalIdFactory);
        final DependencyGraph dependencyGraph = goGradleLockParser.parse(goGradleLockFile);

        final GraphAssert graphAssert = new GraphAssert(Forge.GOLANG, dependencyGraph);
    }
}