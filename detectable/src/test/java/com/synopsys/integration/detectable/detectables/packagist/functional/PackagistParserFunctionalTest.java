package com.synopsys.integration.detectable.detectables.packagist.functional;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.packagist.ComposerLockDetectableOptions;
import com.synopsys.integration.detectable.detectables.packagist.model.PackagistParseResult;
import com.synopsys.integration.detectable.detectables.packagist.parse.PackagistParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphCompare;

public class PackagistParserFunctionalTest {

    @Test
    public void packagistParserTest() throws IOException {
        final ComposerLockDetectableOptions composerLockDetectableOptions = new ComposerLockDetectableOptions(true);
        final PackagistParser packagistParser = new PackagistParser(new ExternalIdFactory(), composerLockDetectableOptions);

        final String composerLockText = FunctionalTestFiles.asString("/packagist/composer.lock");
        final String composerJsonText = FunctionalTestFiles.asString("/packagist/composer.json");
        final PackagistParseResult result = packagistParser.getDependencyGraphFromProject("source", composerJsonText, composerLockText);

        Assert.assertEquals(result.getProjectName(), "clue/graph-composer");
        Assert.assertEquals(result.getProjectVersion(), "1.0.0");

        GraphCompare.assertEqualsResource("/packagist/PackagistTestDependencyNode_graph.json", result.getCodeLocation().getDependencyGraph());
    }
}
