package com.blackduck.integration.detectable.detectables.go.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStream;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.blackduck.integration.detectable.detectables.go.godep.parse.GoLockParser;
import com.blackduck.integration.detectable.util.FunctionalTestFiles;

public class GoLockParserTest {
    @Test
    public void testNoProjects() {
        GoLockParser parser = new GoLockParser();
        InputStream gopkgLockInputStream = FunctionalTestFiles.asInputStream("/go/Gopkg_noprojects.lock");
        DependencyGraph graph = parser.parseDepLock(gopkgLockInputStream);
        assertEquals(0, graph.getRootDependencies().size());
    }

}
