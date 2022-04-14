package com.synopsys.integration.detectable.detectables.conda.functional;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.conda.parser.CondaDependencyCreator;
import com.synopsys.integration.detectable.detectables.conda.parser.CondaListParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;

public class CondaListParserTest {
    @Test
    public void testForgesCorrectlyAssigned() throws IOException {
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        CondaDependencyCreator dependencyCreator = new CondaDependencyCreator(externalIdFactory);
        CondaListParser condaListParser = new CondaListParser(new Gson(), dependencyCreator);

        File condaListFile = FunctionalTestFiles.asFile("/conda/condaListWithPypiAndCondaComponents.txt");
        String condaListText = FileUtils.readFileToString(condaListFile, StandardCharsets.UTF_8);

        DependencyGraph dependencyGraph = condaListParser.parse(condaListText, "{\n\"platform\":\"test\"\n}");

        ExternalId treeliteRuntimeExternalId = externalIdFactory.createNameVersionExternalId(Forge.PYPI, "treelite-runtime", "2.0.0");
        assertTrue(
            dependencyGraph.getRootDependencies().stream()
                .map(Dependency::getExternalId)
                .anyMatch(treeliteRuntimeExternalId::equals)
        );

        ExternalId tkExternalId = externalIdFactory.createNameVersionExternalId(Forge.ANACONDA, "tk", "8.6.11-h5dbffcc_1-test");
        assertTrue(
            dependencyGraph.getRootDependencies().stream()
                .map(Dependency::getExternalId)
                .anyMatch(tkExternalId::equals)
        );

    }
}
