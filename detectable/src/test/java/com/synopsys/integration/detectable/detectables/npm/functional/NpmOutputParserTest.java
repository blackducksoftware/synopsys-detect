package com.synopsys.integration.detectable.detectables.npm.functional;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.npm.parse.NpmCliParser;
import com.synopsys.integration.detectable.detectables.npm.parse.NpmParseResult;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphCompare;

public class NpmOutputParserTest {
    @Test
    public void npmCliDependencyFinder() throws IOException {
        final NpmCliParser parser = new NpmCliParser(new ExternalIdFactory());
        final String testIn = FunctionalTestFiles.asString("/npm/packman_proj_dependencies.json");
        final NpmParseResult result = parser.convertNpmJsonFileToCodeLocation("source", testIn);

        Assert.assertEquals(result.projectName, "node-js");
        Assert.assertEquals(result.projectVersion, "0.2.0");
        GraphCompare.assertEqualsResource("/npm/npmParseOutput_graph.json", result.codeLocation.getDependencyGraph());
    }
}
