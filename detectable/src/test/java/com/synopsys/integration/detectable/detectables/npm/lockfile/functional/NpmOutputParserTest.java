package com.synopsys.integration.detectable.detectables.npm.lockfile.functional;

import org.junit.Assert;
import org.junit.Test;

import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.npm.cli.parse.NpmCliParser;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmParseResult;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphCompare;

public class NpmOutputParserTest {
    @Test
    public void npmCliDependencyFinder() {
        final NpmCliParser parser = new NpmCliParser(new ExternalIdFactory());
        final String testIn = FunctionalTestFiles.asString("/npm/packman_proj_dependencies.json");
        final NpmParseResult result = parser.convertNpmJsonFileToCodeLocation(testIn);

        Assert.assertEquals("node-js", result.getProjectName());
        Assert.assertEquals("0.2.0", result.getProjectVersion());
        GraphCompare.assertEqualsResource("/npm/npmParseOutput_graph.json", result.getCodeLocation().getDependencyGraph());
    }
}
