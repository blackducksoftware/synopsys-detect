package com.synopsys.integration.detectable.detectables.go.vendr.parse;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

class VndrParserTest {
    private static VndrParser vndrParser;

    @BeforeAll
    static void beforeAll() {
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        vndrParser = new VndrParser(externalIdFactory);
    }

    @Test
    void happyPathTest() {
        DependencyGraph dependencyGraph = vndrParser.parseVendorConf(Arrays.asList(
            "github.com/klauspost/compress v1.4.1",
            "github.com/klauspost/cpuid v1.2.0"
        ));
        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.GOLANG, dependencyGraph);
        graphAssert.hasRootSize(2);
        graphAssert.hasRootDependency("github.com/klauspost/compress", "v1.4.1");
        graphAssert.hasRootDependency("github.com/klauspost/cpuid", "v1.2.0");
    }

    @Test
    void invalidLine() {
        DependencyGraph dependencyGraph = vndrParser.parseVendorConf(Arrays.asList(
            "github.com/klauspost/compress v1.4.1",
            "invlaid_line"
        ));
        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.GOLANG, dependencyGraph);
        graphAssert.hasRootSize(1);
        graphAssert.hasRootDependency("github.com/klauspost/compress", "v1.4.1");
    }

    @Test
    void commentsTest() {
        DependencyGraph dependencyGraph = vndrParser.parseVendorConf(Arrays.asList(
            "github.com/Azure/go-ansiterm d6e3b3328b783f23731bc4d058875b0371ff8109 # v0.4.15",
            "github.com/klauspost/compress v1.4.1",
            "# github.com/Azure/go-ansiterm dont_include_me"
        ));
        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.GOLANG, dependencyGraph);
        graphAssert.hasRootSize(2);
        graphAssert.hasRootDependency("github.com/Azure/go-ansiterm", "d6e3b3328b783f23731bc4d058875b0371ff8109");
        graphAssert.hasRootDependency("github.com/klauspost/compress", "v1.4.1");
    }

    @Test
    void whitespaceSeparationTest() {
        DependencyGraph dependencyGraph = vndrParser.parseVendorConf(Arrays.asList(
            "github.com/Microsoft/go-winio v1",
            "github.com/Microsoft/go-winio   v2",
            "github.com/Microsoft/go-winio\tv3"
        ));
        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.GOLANG, dependencyGraph);
        graphAssert.hasRootSize(3);
        graphAssert.hasRootDependency("github.com/Microsoft/go-winio", "v1");
        graphAssert.hasRootDependency("github.com/Microsoft/go-winio", "v2");
        graphAssert.hasRootDependency("github.com/Microsoft/go-winio", "v3");
    }
}
