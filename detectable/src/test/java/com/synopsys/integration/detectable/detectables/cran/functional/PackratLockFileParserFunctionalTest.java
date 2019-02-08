package com.synopsys.integration.detectable.detectables.cran.functional;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.cran.PackratLockFileParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphCompare;

class PackratLockFileParserFunctionalTest {
    @Test
    void parseProjectDependencies() {
        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final PackratLockFileParser packRatLockFileParser = new PackratLockFileParser(externalIdFactory);
        final List<String> packratFileLines = FunctionalTestFiles.asListOfStrings("/cran/packrat.lock");
        final DependencyGraph actualDependencyGraph = packRatLockFileParser.parseProjectDependencies(packratFileLines);

        GraphCompare.assertEqualsResource("/cran/expectedDependencyGraph.json", actualDependencyGraph);
    }
}