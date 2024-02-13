package com.synopsys.integration.detectable.detectables.pip.parser.functional;


import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectables.pip.parser.RequirementsFileDependencyTransformer;
import com.synopsys.integration.detectable.detectables.pip.parser.RequirementsFileExtractor;
import com.synopsys.integration.detectable.detectables.pip.parser.RequirementsFileTransformer;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

@FunctionalTest
class RequirementsFileDetectableTest {
    private static File requirementsFile;
    private static RequirementsFileDependencyTransformer requirementsFileDependencyTransformer;
    private static RequirementsFileTransformer requirementsFileTransformer;
    private static RequirementsFileExtractor requirementsFileExtractor;
    private static ExternalId testDependency1;
    private static ExternalId testDependency2;
    private static ExternalId testDependency3;
    private static ExternalId testDependency4;
    private static ExternalId testDependency5;

    @BeforeAll
    protected static void setUp() {
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        testDependency1 = externalIdFactory.createNameVersionExternalId(Forge.PYPI, "name1", "");
        testDependency2 = externalIdFactory.createNameVersionExternalId(Forge.PYPI, "name2", "1.2.3");
        testDependency3 = externalIdFactory.createNameVersionExternalId(Forge.PYPI, "name3", "1.2");
        testDependency4 = externalIdFactory.createNameVersionExternalId(Forge.PYPI, "name4", "0.5.3");
        testDependency5 = externalIdFactory.createNameVersionExternalId(Forge.PYPI, "name5", "1.2.3");

        requirementsFile = new File("src/test/resources/detectables/functional/pip/requirements.txt");

        requirementsFileTransformer = new RequirementsFileTransformer();
        requirementsFileDependencyTransformer = new RequirementsFileDependencyTransformer();
        requirementsFileExtractor = new RequirementsFileExtractor(requirementsFileTransformer, requirementsFileDependencyTransformer);
    }

    @Test
    void testDependencyExtractionFromRequirementsFile() throws IOException {
        List<File> requirementsFiles = Collections.singletonList(requirementsFile);
        Extraction testFileExtraction = requirementsFileExtractor.extract(requirementsFiles);
        Assertions.assertEquals(1, testFileExtraction.getCodeLocations().size());

        DependencyGraph testDependencyGraph = testFileExtraction.getCodeLocations().get(0).getDependencyGraph();

        NameVersionGraphAssert nameVersionGraphAssert = new NameVersionGraphAssert(Forge.PYPI, testDependencyGraph);
        nameVersionGraphAssert.hasDependency(testDependency1.getName(), testDependency1.getVersion());
        nameVersionGraphAssert.hasDependency(testDependency2.getName(), testDependency2.getVersion());
        nameVersionGraphAssert.hasDependency(testDependency3.getName(), testDependency3.getVersion());
        nameVersionGraphAssert.hasDependency(testDependency4.getName(), testDependency4.getVersion());
        nameVersionGraphAssert.hasDependency(testDependency5.getName(), testDependency5.getVersion());

    }
}
