package com.blackducksoftware.integration.hub.detect.bomtool.npm;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphResourceTestUtil;
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class NpmOutputParserTest {
    private final TestUtil testUtil = new TestUtil();

    @Test
    public void npmCliDependencyFinder() throws IOException {
        final NpmCliParser parser = new NpmCliParser(new ExternalIdFactory());
        final String testIn = testUtil.getResourceAsUTF8String("/npm/packman_proj_dependencies.json");
        final NpmParseResult result = parser.convertNpmJsonFileToCodeLocation(BomToolType.NPM_CLI, "source", testIn);

        Assert.assertEquals(result.projectName, "node-js");
        Assert.assertEquals(result.projectVersion, "0.2.0");
        DependencyGraphResourceTestUtil.assertGraph("/npm/npmParseOutput_graph.json", result.codeLocation.getDependencyGraph());
    }
}
