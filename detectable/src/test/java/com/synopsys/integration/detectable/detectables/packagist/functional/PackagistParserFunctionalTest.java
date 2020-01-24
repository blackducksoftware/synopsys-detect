package com.synopsys.integration.detectable.detectables.packagist.functional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;

import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.packagist.ComposerLockDetectableOptions;
import com.synopsys.integration.detectable.detectables.packagist.model.PackagistParseResult;
import com.synopsys.integration.detectable.detectables.packagist.parse.PackagistParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;

public class PackagistParserFunctionalTest {

    @Test
    @Disabled
    public void packagistParserTest() throws MissingExternalIdException {
        final ComposerLockDetectableOptions composerLockDetectableOptions = new ComposerLockDetectableOptions(true);
        final PackagistParser packagistParser = new PackagistParser(new ExternalIdFactory(), composerLockDetectableOptions);

        final String composerLockText = FunctionalTestFiles.asString("/packagist/composer.lock");
        final String composerJsonText = FunctionalTestFiles.asString("/packagist/composer.json");
        final PackagistParseResult result = packagistParser.getDependencyGraphFromProject(composerJsonText, composerLockText);

        Assert.assertEquals("clue/graph-composer", result.getProjectName());
        Assert.assertEquals("1.0.0", result.getProjectVersion());

        //GraphCompare.assertEqualsResource("/packagist/PackagistTestDependencyNode_graph.json", result.getCodeLocation().getDependencyGraph());
    }
}
