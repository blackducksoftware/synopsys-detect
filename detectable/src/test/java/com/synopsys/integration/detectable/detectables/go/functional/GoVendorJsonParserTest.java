package com.synopsys.integration.detectable.detectables.go.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.go.vendor.parse.GoVendorJsonParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;

public class GoVendorJsonParserTest {

    @Test
    public void test() throws IOException {
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        GoVendorJsonParser parser = new GoVendorJsonParser(externalIdFactory);
        DependencyGraph graph = parser.parseVendorJson(new Gson(), FunctionalTestFiles.asString("/go/vendor/vendor.json"));
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
