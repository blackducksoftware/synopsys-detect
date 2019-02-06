package com.synopsys.integration.detectable.detectables.cran.functional;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.cran.PackRatLockFileParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphAssert;

class PackRatLockFileParserFunctionalTest {
    @Test
    void parseProjectDependencies() {
        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final PackRatLockFileParser packRatLockFileParser = new PackRatLockFileParser(externalIdFactory);
        final List<String> packratFileLines = FunctionalTestFiles.asListOfStrings("/cran/packrat.lock");
        final DependencyGraph actualDependencyGraph = packRatLockFileParser.parseProjectDependencies(packratFileLines);

        GraphAssert.assertGraph("/cran/expectedDependencyGraph.json", actualDependencyGraph);
    }
}