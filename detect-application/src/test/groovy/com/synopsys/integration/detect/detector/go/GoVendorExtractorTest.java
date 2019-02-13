package com.synopsys.integration.detect.detector.go;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.synopsys.integration.detect.workflow.extraction.Extraction;
import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class GoVendorExtractorTest {

    @Test
    public void test() {
        GoVendorExtractor extractor = new GoVendorExtractor(new Gson(), new ExternalIdFactory());
        Extraction extraction = extractor.extract(new File("src/test/resources/go"), new File("src/test/resources/go/vendor/vendor.json"));
        DependencyGraph graph = extraction.codeLocations.get(0).getDependencyGraph();
        assertEquals(2, graph.getRootDependencies().size());
        boolean foundErrorsPkg = false;
        boolean foundMathPkg = false;
        for (Dependency dep : graph.getRootDependencies()) {
            if ("github.com/pkg/errors".equals(dep.name)) {
                foundErrorsPkg = true;
                assertEquals("github.com/pkg/errors", dep.externalId.name);
                assertEquals("059132a15dd08d6704c67711dae0cf35ab991756", dep.externalId.version);
            }
            if ("github.com/pkg/math".equals(dep.name)) {
                foundMathPkg = true;
                assertEquals("github.com/pkg/math", dep.externalId.name);
                assertEquals("f2ed9e40e245cdeec72c4b642d27ed4553f90667", dep.externalId.version);
            }
        }
        assertTrue(foundErrorsPkg);
        assertTrue(foundMathPkg);
    }
}
