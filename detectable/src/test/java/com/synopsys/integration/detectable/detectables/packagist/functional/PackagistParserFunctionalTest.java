package com.synopsys.integration.detectable.detectables.packagist.functional;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.packagist.ComposerLockDetectableOptions;
import com.synopsys.integration.detectable.detectables.packagist.PackagistParseResult;
import com.synopsys.integration.detectable.detectables.packagist.PackagistParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.graph.GraphAssert;

public class PackagistParserFunctionalTest {

    @Test
    public void packagistParserTest() throws IOException {
        final ComposerLockDetectableOptions composerLockDetectableOptions = new ComposerLockDetectableOptions(true);
        final PackagistParser packagistParser = new PackagistParser(new ExternalIdFactory(), composerLockDetectableOptions);

        final String composerLockText = FunctionalTestFiles.asString("/packagist/composer.lock");
        final String composerJsonText = FunctionalTestFiles.asString("/packagist/composer.json");
        final PackagistParseResult result = packagistParser.getDependencyGraphFromProject("source", composerJsonText, composerLockText);

        Assert.assertEquals(result.projectName, "clue/graph-composer");
        Assert.assertEquals(result.projectVersion, "1.0.0");

        GraphAssert.assertGraph("/packagist/PackagistTestDependencyNode_graph.json", result.codeLocation.getDependencyGraph());
    }
}
